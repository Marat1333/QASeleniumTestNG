package com.leroy.core.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leroy.core.ContextProvider;
import com.leroy.core.annotations.DisableTestWhen;
import com.leroy.core.annotations.Smoke;
import com.leroy.core.configuration.BaseUiTest;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.core.configuration.Log;
import com.leroy.core.configuration.TestInfo;
import com.leroy.core.listeners.helpers.RetryAnalyzer;
import com.leroy.core.listeners.helpers.XMLSuiteResultWriter;
import com.leroy.core.pages.AnyPage;
import io.qameta.allure.Issue;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.*;
import org.testng.annotations.ITestAnnotation;
import org.testng.internal.Utils;
import org.testng.reporters.XMLReporterConfig;
import org.testng.reporters.XMLStringBuffer;
import org.testng.util.Strings;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Listener implements ITestListener, ISuiteListener,
        IInvokedMethodListener, IReporter, IAnnotationTransformer {

    private String BROWSER_PROFILE;
    private boolean outputDirExist = false;
    private Map<String, String> outputConfig = new HashMap<String, String>();
    private int testPassed = 0;
    private int testFailed = 0;
    private int testSkipped = 0;
    private static final String TEST_CASE_ID_PATTERN = "C\\d+";
    private static final ThreadLocal<String> context = new ThreadLocal<>();

    public static final String FILE_NAME = "testng-results.xml";

    protected final XMLReporterConfig config = new XMLReporterConfig();
    private XMLStringBuffer rootBuffer;

    public Listener() {
        BROWSER_PROFILE = readBrowserFromPropertyFile();
    }

    //right now only AFTER_SUITE and AFTER_ALL methods are working correctly
    enum ResultGenerationMode {
        AFTER_TEST, AFTER_CLASS, AFTER_SUITE, AFTER_ALL
    }

    private static Listener.ResultGenerationMode resultGenerationMode;

    private ArrayList<ISuite> suites = new ArrayList<>();
    private static boolean processFail;
    private static boolean disableScreenshots;
    protected String currentScreenshotPath;


    private static ObjectMapper mapper = new ObjectMapper();

    static {
        try {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            String mode = System.getProperty("resultGenerationMode", "all");
            switch (mode) {
                case "test":
                    resultGenerationMode = Listener.ResultGenerationMode.AFTER_TEST;
                    break;
                case "class":
                    resultGenerationMode = Listener.ResultGenerationMode.AFTER_CLASS;
                    break;
                case "suite":
                    resultGenerationMode = Listener.ResultGenerationMode.AFTER_SUITE;
                    break;
                default:
                    resultGenerationMode = Listener.ResultGenerationMode.AFTER_ALL;
                    break;
            }

            processFail = Boolean.parseBoolean(System.getProperty("processFail", "true"));
            disableScreenshots = Boolean.parseBoolean(System.getProperty("disableScreenshots", "false"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Retry analyzer
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
        if (isTestNeedToDisable(testMethod)) {
            annotation.setEnabled(false);
        }
    }

    private String readBrowserFromPropertyFile() {
        String pathPropsFile = System.getProperty("mpropsFile");
        if (pathPropsFile == null)
            return null;
        Map<String, Object> properties = (Map<String, Object>)
                DriverFactory.loadPropertiesFromFile(pathPropsFile);
        Map<String, Object> settings = (Map<String, Object>) properties.get("settings");
        return DriverFactory.getPropertyValue("", settings, "browser",
                System.getProperty("mBrowser"));
    }

    private boolean isTestNeedToDisable(Method method) {
        String smoke = System.getProperty("smoke");
        String withIssues = System.getProperty("runWithIssues");
        if (Strings.isNotNullAndNotEmpty(withIssues) && withIssues.equalsIgnoreCase("false")) {
            return method.isAnnotationPresent(Issue.class);
        }
        if (Strings.isNotNullAndNotEmpty(smoke) && smoke.equals("true")) {
            return !method.isAnnotationPresent(Smoke.class);
        }
        if (method.isAnnotationPresent(DisableTestWhen.class)) {
            String[] browsers = method.getAnnotation(DisableTestWhen.class).browsers();
            return Arrays.asList(browsers).contains(BROWSER_PROFILE);
        }
        return false;
    }

    // This belongs to ISuiteListener and will execute before the Suite start
    @Override
    public void onStart(ISuite arg0) {
        System.setProperty("current.date", new SimpleDateFormat("E_yyyy.MM.dd_HH.mm.ss_z").format(new Date()));
        arg0.getXmlSuite().setName(arg0.getName());

        // Continue with the rest of the initialization of the system properties
        if (!outputDirExist) {

            System.setProperty("suite.name", "TestResults");
            System.setProperty(
                    "output.path",
                    "data-output/" + "__Run_"
                            + System.getProperty("suite.name") + "_"
                            + System.getProperty("current.date"));

            // if TestDataOutput doesn't exist, create it
            File TestDataOutputDir = new File(System.getProperty("output.path"));

            if (!TestDataOutputDir.exists()) {
                TestDataOutputDir.mkdirs();
            }

            this.outputConfig.put("outputDir",
                    System.getProperty("output.path"));
            this.outputConfig.put("ExecutionStatus", "started");
            this.outputConfig.put("Started_Time",
                    new SimpleDateFormat("E_yyyy.MM.dd_HH.mm.ss_z").format(new Date()));
            generateRunConfig();

            outputDirExist = true;
        }

        Log.info("About to begin executing Suite " + arg0.getName());
        System.setProperty("suitename", arg0.getName());
        String threadCount = System.getProperty("threadCount");
        // Setting the environment parameters
        if (threadCount != null) {
            arg0.getXmlSuite().setThreadCount(Integer.parseInt(threadCount));
            arg0.getXmlSuite().setPreserveOrder(true);
        }

        suites.add(arg0);
    }

    @Override
    // This belongs to ISuiteListener and will execute, once the Suite is
    // finished
    public void onFinish(ISuite arg0) {
        if (resultGenerationMode == Listener.ResultGenerationMode.AFTER_SUITE) {
            generateReport();
        }
    }

    private void generateReport() {
        String outDir = config.getOutputDirectory();
        if (outDir == null || outDir.isEmpty()) {
            outDir = "test-output";
        }
        generateReport(null, suites, outDir);
    }

    // This belongs to ITestListener and will execute before starting of Test
    // set/batch
    @Override
    public void onStart(ITestContext arg0) {
        Log.startTestCase(arg0.getName());
        Log.info("About to begin executing Test " + arg0.getName());
        //find out Test Case ID and set in threadLocal for DriverFactory to set up tag
        Pattern testIDpattern = Pattern.compile(TEST_CASE_ID_PATTERN);
        Matcher matcher = testIDpattern.matcher(arg0.getName());
        if (matcher.find()) {
            Log.info("@@@Test Case ID: " + matcher.group(0));
            context.set(matcher.group(0));
        }
    }

    // This belongs to ITestListener and will execute, once the Test set/batch
    // is finished
    @Override
    public void onFinish(ITestContext arg0) {
        Log.info("Completed executing test " + arg0.getName());
        Log.endTestCase(arg0.getName());
    }

    // This belongs to ITestListener and will execute only when the test is passed
    @Override
    public void onTestSuccess(ITestResult arg0) {
        printTestResults(arg0);
    }

    // This belongs to ITestListener and will execute only when the test is failed
    @Override
    public void onTestFailure(ITestResult arg0) {
        printTestResults(arg0);
    }

    // This belongs to ITestListener and will execute before the main test start
    // (@Test)
    @Override
    public void onTestStart(ITestResult arg0) {
        currentScreenshotPath = null;
        Object currentClass = arg0.getInstance();
        try {
            RemoteWebDriver driver = (RemoteWebDriver) ContextProvider.getDriver();
            if (driver != null) {
                Log.info("Test started on the following configuration " + driver.getCapabilities().toString());
                // Add run configuration
                arg0.setAttribute("configuration::Browser", System.getProperty("mbrowserfullname"));
                arg0.setAttribute("configuration::Platform", System.getProperty("mplatformfullname"));
                arg0.setAttribute("configuration::Environment", System.getProperty("menv"));
                setGenerateTestResultAttributes(true);
            }
        } catch (Exception e) {
            // do nothing.
        }
    }

    // This belongs to ITestListener and will execute only if any of the main
    // test(@Test) get skipped
    @Override
    public void onTestSkipped(ITestResult arg0) {
        Log.info("Skipping Test");
        printTestResults(arg0);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult arg0) {
        //Not implemented at the moment
    }

    // This belongs to IInvokedMethodListener and will execute before every
    // method including @Before @After @Test
    @Override
    public void beforeInvocation(IInvokedMethod arg0, ITestResult arg1) {
        Log.info("Started execution of the following method: "
                + returnMethodName(arg0.getTestMethod()));
    }

    // This belongs to IInvokedMethodListener and will execute after every
    // method including @Before @After @Test
    @Override
    public void afterInvocation(IInvokedMethod arg0, ITestResult arg1) {
        if (processFail) {
            if (arg1.getStatus() == ITestResult.FAILURE)
                updateResultWithScreenshot(arg1);
        }
        try {
            ITestNGMethod testMethod = arg0.getTestMethod();

            if (resultGenerationMode == Listener.ResultGenerationMode.AFTER_CLASS
                    && testMethod.isAfterClassConfiguration() && !testMethod.hasMoreInvocation()) {
                generateReport();
            } else if (resultGenerationMode == Listener.ResultGenerationMode.AFTER_TEST && testMethod.isTest()) {
                generateReport();
            }

            if (!arg1.getMethod().isTest()) {
                printTestResults(arg1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites,
                               String outputDirectory) {
        if (Utils.isStringEmpty(config.getOutputDirectory())) {
            config.setOutputDirectory(outputDirectory);
        }

        // Calculate passed/failed/skipped
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        for (ISuite s : suites) {
            Map<String, ISuiteResult> suiteResults = s.getResults();
            synchronized (suiteResults) {
                for (ISuiteResult sr : suiteResults.values()) {
                    ITestContext testContext = sr.getTestContext();
                    passed += testContext.getPassedTests().size();
                    failed += testContext.getFailedTests().size();
                    skipped += testContext.getSkippedTests().size();
                }
            }
        }

        rootBuffer = new XMLStringBuffer();
        Properties p = new Properties();
        p.put("passed", passed);
        p.put("failed", failed);
        p.put("skipped", skipped);
        p.put("total", passed + failed + skipped);
        rootBuffer.push(XMLReporterConfig.TAG_TESTNG_RESULTS, p);
        // skipped the full report-output in favor of individual suite
        // output
        // writeReporterOutput(rootBuffer);
        for (ISuite suite : suites) {
            writeSuite(suite.getXmlSuite(), suite);
        }
        rootBuffer.pop();
        if (config.getOutputDirectory().contains("surefire-reports"))
            Utils.writeUtf8File("test-output", FILE_NAME, rootBuffer,
                    null /* no prefix */);
        Utils.writeUtf8File(config.getOutputDirectory(), FILE_NAME, rootBuffer,
                null /* no prefix */);
    }

    /**
     * This method is used to spit out the first cummulative reporter-outut
     * It's not being used right now in favor of the individual reporter-output
     * for each suite
     *
     * @param xmlBuffer
     */
    private void writeReporterOutput(XMLStringBuffer xmlBuffer) {
        xmlBuffer.push(XMLReporterConfig.TAG_REPORTER_OUTPUT);
        List<String> output = Reporter.getOutput();
        for (String line : output) {
            if (line != null) {
                xmlBuffer.push(XMLReporterConfig.TAG_LINE);
                xmlBuffer.addCDATA(XMLSuiteResultWriter.filterInvalidChars(line));
                xmlBuffer.pop();
            }
        }
        xmlBuffer.pop();
    }

    private void writeSuite(XmlSuite xmlSuite, ISuite suite) {
        switch (config.getFileFragmentationLevel()) {
            case XMLReporterConfig.FF_LEVEL_NONE:
                writeSuiteToBuffer(rootBuffer, suite);
                break;
            case XMLReporterConfig.FF_LEVEL_SUITE:
            case XMLReporterConfig.FF_LEVEL_SUITE_RESULT:
                File suiteFile = referenceSuite(rootBuffer, suite);
                writeSuiteToFile(suiteFile, suite);
                break;
            default:
                throw new AssertionError("Unexpected value: "
                        + config.getFileFragmentationLevel());
        }
    }

    private void writeSuiteToFile(File suiteFile, ISuite suite) {
        XMLStringBuffer xmlBuffer = new XMLStringBuffer();
        writeSuiteToBuffer(xmlBuffer, suite);
        File parentDir = suiteFile.getParentFile();
        if (parentDir.exists() || suiteFile.getParentFile().mkdirs()) {
            Utils.writeFile(parentDir.getAbsolutePath(), FILE_NAME,
                    xmlBuffer.toXML());
        }
    }

    private File referenceSuite(XMLStringBuffer xmlBuffer, ISuite suite) {
        String relativePath = suite.getName() + File.separatorChar + FILE_NAME;
        File suiteFile = new File(config.getOutputDirectory(), relativePath);
        Properties attrs = new Properties();
        attrs.setProperty(XMLReporterConfig.ATTR_URL, relativePath);
        xmlBuffer.addEmptyElement(XMLReporterConfig.TAG_SUITE, attrs);
        return suiteFile;
    }

    private void writeSuiteToBuffer(XMLStringBuffer xmlBuffer, ISuite suite) {
        xmlBuffer.push(XMLReporterConfig.TAG_SUITE, getSuiteAttributes(suite));
        writeSuiteGroups(xmlBuffer, suite);

        Map<String, ISuiteResult> results = suite.getResults();
        XMLSuiteResultWriter suiteResultWriter = getSuiteResultWriter();
        for (Map.Entry<String, ISuiteResult> result : results.entrySet()) {
            suiteResultWriter.writeSuiteResult(xmlBuffer, result.getValue());
        }

        xmlBuffer.pop();
    }

    protected XMLSuiteResultWriter getSuiteResultWriter() {
        return new XMLSuiteResultWriter(config);
    }

    private void generateRunConfig() {
        try {
            Properties properties = new Properties();
            for (String key : this.outputConfig.keySet()) {
                properties.setProperty(key, this.outputConfig.get(key));
            }
            File file = new File("runConfiguration.properties");
            FileOutputStream fileOut = new FileOutputStream(file);
            properties.store(fileOut, "Framework Run Configuration");
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeSuiteGroups(XMLStringBuffer xmlBuffer, ISuite suite) {
        xmlBuffer.push(XMLReporterConfig.TAG_GROUPS);
        Map<String, Collection<ITestNGMethod>> methodsByGroups = suite
                .getMethodsByGroups();
        for (Map.Entry<String, Collection<ITestNGMethod>> entry : methodsByGroups
                .entrySet()) {
            Properties groupAttrs = new Properties();
            groupAttrs.setProperty(XMLReporterConfig.ATTR_NAME, entry.getKey());
            xmlBuffer.push(XMLReporterConfig.TAG_GROUP, groupAttrs);
            Set<ITestNGMethod> groupMethods = getUniqueMethodSet(entry
                    .getValue());
            for (ITestNGMethod groupMethod : groupMethods) {
                Properties methodAttrs = new Properties();
                methodAttrs.setProperty(XMLReporterConfig.ATTR_NAME,
                        groupMethod.getMethodName());
                methodAttrs.setProperty(XMLReporterConfig.ATTR_METHOD_SIG,
                        groupMethod.toString());
                methodAttrs.setProperty(XMLReporterConfig.ATTR_CLASS,
                        groupMethod.getRealClass().getName());
                xmlBuffer.addEmptyElement(XMLReporterConfig.TAG_METHOD,
                        methodAttrs);
            }
            xmlBuffer.pop();
        }
        xmlBuffer.pop();
    }

    private Properties getSuiteAttributes(ISuite suite) {
        Properties props = new Properties();
        props.setProperty(XMLReporterConfig.ATTR_NAME, suite.getName());

        // Calculate the duration
        Map<String, ISuiteResult> results = suite.getResults();
        Date minStartDate = new Date();
        Date maxEndDate = null;
        synchronized (results) {
            for (Map.Entry<String, ISuiteResult> result : results.entrySet()) {
                ITestContext testContext = result.getValue().getTestContext();
                Date startDate = testContext.getStartDate();
                Date endDate = testContext.getEndDate();
                if (minStartDate.after(startDate)) {
                    minStartDate = startDate;
                }
                if (maxEndDate == null || maxEndDate.before(endDate)) {
                    maxEndDate = endDate != null ? endDate : startDate;
                }
            }
        }
        // The suite could be completely empty
        if (maxEndDate == null) {
            maxEndDate = minStartDate;
        }
        addDurationAttributes(config, props, minStartDate, maxEndDate);
        return props;
    }

    /**
     * Add started-at, finished-at and duration-ms attributes to the <suite> tag
     */
    public static void addDurationAttributes(XMLReporterConfig config,
                                             Properties attributes, Date minStartDate, Date maxEndDate) {
        SimpleDateFormat format = new SimpleDateFormat(
                config.getTimestampFormat());
        TimeZone utc = TimeZone.getTimeZone("UTC");
        format.setTimeZone(utc);
        String startTime = format.format(minStartDate);
        String endTime = format.format(maxEndDate);
        long duration = maxEndDate.getTime() - minStartDate.getTime();

        attributes.setProperty(XMLReporterConfig.ATTR_STARTED_AT, startTime);
        attributes.setProperty(XMLReporterConfig.ATTR_FINISHED_AT, endTime);
        attributes.setProperty(XMLReporterConfig.ATTR_DURATION_MS,
                Long.toString(duration));
    }

    private Set<ITestNGMethod> getUniqueMethodSet(
            Collection<ITestNGMethod> methods) {
        Set<ITestNGMethod> result = new LinkedHashSet<>();
        for (ITestNGMethod method : methods) {
            result.add(method);
        }
        return result;
    }

    public void setGenerateTestResultAttributes(
            boolean generateTestResultAttributes) {
        config.setGenerateTestResultAttributes(generateTestResultAttributes);
    }

    // This will return method names to the calling function
    private String returnMethodName(ITestNGMethod method) {
        return method.getRealClass().getSimpleName() + "." + method.getMethodName();
    }

    public static String getTestCaseID() {
        return context.get();
    }

    public static Map<String, String> getSauceLabsCredentials() {
        Map<String, String> credentials = new HashMap<String, String>();
        String mhubipport = System.getProperty(DriverFactory.HOST_ENV_VAR);
        if (StringUtils.isNotEmpty(mhubipport)) {
            String[] splited_mhubipport = mhubipport.split("(:)");
            credentials.put("username", splited_mhubipport[0].replace("%40", "@"));
            credentials.put("port", splited_mhubipport[2]);
            splited_mhubipport = splited_mhubipport[1].split("(:)|(@)");
            credentials.put("password", splited_mhubipport[0]);
            credentials.put("host", splited_mhubipport[1]);
        } else {
            Log.error("Property mhubipport is empty or null");
        }

        return credentials;
    }

    protected String getTestCaseId(ITestResult arg0) {
        String result = "UNDEFINED_TEST_CASE_ID";
        if (arg0.getMethod().getDescription() != null) {
            Matcher matcher = Pattern.compile(TEST_CASE_ID_PATTERN).matcher(arg0.getMethod().getDescription());
            if (matcher.find()) {
                result = matcher.group(0);
            } else {
                Log.error("Couldn't define CASE ID for '" + arg0.getMethod().getDescription() + "'");
            }
        }
        return result;
    }

    private void updateResultWithScreenshot(ITestResult arg0) {
        if (arg0.getInstance() instanceof BaseUiTest && !disableScreenshots) {
            RemoteWebDriver driver = (RemoteWebDriver) ContextProvider.getDriver();
            if (driver != null) {
                try {
                    String screenShotName = getTestCaseId(arg0);
                    screenShotName = screenShotName + "_" + RandomStringUtils.randomNumeric(6);
                    String screenShotPath = new AnyPage()
                            .takeScreenShot(screenShotName + ".png");
                    currentScreenshotPath = screenShotPath;
                    arg0.setAttribute("screenshot", screenShotPath);
                    setGenerateTestResultAttributes(true);
                    Log.info("Screenshot path: " + screenShotPath);
                } catch (Exception e) {
                    Log.error("Results wasn't updated with screenshot. Error: " + e.getMessage());
                }
            }
        }
    }

    // This will provide the information on the test
    private void printTestResults(ITestResult result) {
        //Log.info("Test Method resides in " + result.getTestClass().getName());
        if (result.getParameters().length != 0) {
            StringBuilder params = new StringBuilder();
            for (Object parameter : result.getParameters()) {
                params.append(parameter.toString()).append(",");
            }
            Log.info("Test Method had the following parameters : " + params);
        }
        String status = null;
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                status = "Pass";
                this.testPassed++;
                break;
            case ITestResult.FAILURE:
                status = "Failed";
                this.testFailed++;
                break;
            case ITestResult.SKIP:
                status = "Skipped";
                this.testSkipped++;
        }

        try {
            long duration = result.getEndMillis() - result.getStartMillis();
            String json
                    = mapper.writeValueAsString(
                    new TestInfo
                            (result.getTestClass().getName(),
                                    result.getMethod().getMethodName(),
                                    result.getMethod().getDescription(),
                                    status,
                                    !result.getMethod().isTest(),
                                    duration));

            Log.info("Test info: " + json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (status != null && status.equals("Failed")) {
            Log.error(result.getThrowable().getMessage());
        }
    }

}
