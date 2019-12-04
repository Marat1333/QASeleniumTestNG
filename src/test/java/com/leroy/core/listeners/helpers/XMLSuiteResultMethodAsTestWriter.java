package com.leroy.core.listeners.helpers;

import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.collections.Sets;
import org.testng.reporters.XMLReporterConfig;
import org.testng.reporters.XMLStringBuffer;

import java.util.*;

/**
 * Utility writing an ISuiteResult to an XMLStringBuffer. Depending on the settings in the <code>config</code> property
 * it might generate an additional XML file with the actual content and only reference the file with an <code>url</code>
 * attribute in the passed XMLStringBuffer.
 */

public class XMLSuiteResultMethodAsTestWriter extends XMLSuiteResultWriter {


    public XMLSuiteResultMethodAsTestWriter(XMLReporterConfig config) {
        super(config);
    }


    @Override
    protected void writeAllToBuffer(XMLStringBuffer xmlBuffer, ISuiteResult suiteResult) {

        Set<ITestResult> testResults = Sets.newHashSet();
        ITestContext testContext = suiteResult.getTestContext();
        addAllTestResults(testResults, testContext.getPassedTests());
        addAllTestResults(testResults, testContext.getFailedTests());
        addAllTestResults(testResults, testContext.getSkippedTests());
        addAllTestResults(testResults, testContext.getPassedConfigurations());
        addAllTestResults(testResults, testContext.getSkippedConfigurations());
        addAllTestResults(testResults, testContext.getFailedConfigurations());
        addAllTestResults(testResults, testContext.getFailedButWithinSuccessPercentageTests());
        addTestResults(xmlBuffer, testResults);

    }



    private Properties getTestAttributes(ITestResult testResult) {
        Properties attributes = new Properties();
        ITestContext tc = testResult.getTestContext();
        attributes.setProperty(XMLReporterConfig.ATTR_NAME, testResult.getMethod().getDescription());
        //XMLReporter.addDurationAttributes(config, attributes, tc.getStartDate(), tc.getEndDate());
        long duration = testResult.getEndMillis() - testResult.getStartMillis();
        String strDuration = Long.toString(duration);
        attributes.setProperty("duration-ms", strDuration);
        return attributes;
    }

    @Override
    protected void addTestResults(XMLStringBuffer xmlBuffer, Set<ITestResult> testResults) {
        Map<String, List<ITestResult>> testsGroupedByClass = buildTestClassGroups(testResults);
        for (Map.Entry<String, List<ITestResult>> result : testsGroupedByClass.entrySet()) {
            Properties attributes = new Properties();
            String className = result.getKey();
            if (config.isSplitClassAndPackageNames()) {
                int dot = className.lastIndexOf('.');
                attributes.setProperty(XMLReporterConfig.ATTR_NAME,
                        dot > -1 ? className.substring(dot + 1, className.length()) : className);
                attributes.setProperty(XMLReporterConfig.ATTR_PACKAGE, dot > -1 ? className.substring(0, dot) : "[default]");
            } else {
                attributes.setProperty(XMLReporterConfig.ATTR_NAME, className);
            }

            List<ITestResult> sortedResults = result.getValue();
            Collections.sort( sortedResults );
            for (ITestResult testResult : sortedResults) {
                addTestResult(xmlBuffer, testResult, attributes);
            }
        }
    }


    protected void addTestResult(XMLStringBuffer xmlBuffer, ITestResult testResult, Properties classAttr) {

        if (!testResult.getMethod().isTest()) {
            return;
        }

        xmlBuffer.push(XMLReporterConfig.TAG_TEST, getTestAttributes(testResult));
        xmlBuffer.push(XMLReporterConfig.TAG_CLASS, classAttr);
        Properties attribs = getTestResultAttributes(testResult);
        String status = getStatusString(testResult);
        attribs.setProperty(XMLReporterConfig.ATTR_STATUS, status);
        xmlBuffer.push(XMLReporterConfig.TAG_TEST_METHOD, attribs);
        addTestMethodParams(xmlBuffer, testResult);
        addTestResultException(xmlBuffer, testResult);
        addTestResultOutput(xmlBuffer, testResult);
        if (config.isGenerateTestResultAttributes()) {
            addTestResultAttributes(xmlBuffer, testResult);
        }
        xmlBuffer.pop();
        xmlBuffer.pop();
        xmlBuffer.pop();
    }

}


