package com.leroy.core.configuration;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.testng.util.Strings;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DriverFactory {
    public static final String HOST_ENV_VAR = "mhost";

    // BROWSERS
    // - Desktop
    public static final String DESKTOP_CHROME_PROFILE = "chrome";
    public static final String DESKTOP_FIREFOX_PROFILE = "firefox";
    public static final String DESKTOP_SAFARI_PROFILE = "safari";
    public static final String DESKTOP_EDGE_PROFILE = "edge";
    public static final String DESKTOP_IE_PROFILE = "ie";
    // - Mobile
    public static final String EMULATOR_MOBILE_CHROME_PROFILE = "mobilechrome";
    public static final String ANDROID_BROWSER_PROFILE = "android_browser";
    public static final String ANDROID_APP_PROFILE = "android_app";
    public static final String IOS_PROFILE = "ios";

    // Key with which to obtain the context.
    private static final String DEFAULT_HOST_VALUE = "localhost";
    private static final String PLATFORM_VERSION_KEY = "platformVersion";
    private static final String BROWSER_VERSION_KEY = "browserVersion";

    // Default value for version of local driver.
    private static String LOCAL_DRIVER_VERSION = "latest";

    // Default implicitly wait timeout, in seconds.
    public static int IMPLICIT_WAIT_TIME_OUT = 30;

    // variables which can be used in tests
    public static String BUILD = "";
    public static String PLATFORM = "";
    public static String BROWSER_PROFILE = "";

    private static MutableCapabilities capabilities = null;

    public static RemoteWebDriver createDriver(String pathToFile,
                                               String platform,
                                               String browser,
                                               String host,
                                               String timeout,
                                               String buildVersion) throws Exception {

        //1. Load the whole set of the properties
        Map<String, Object> properties = (Map<String, Object>) loadPropertiesFromFile(pathToFile);
        Map<String, Object> settings = (Map<String, Object>) properties.get("settings");
        Map<String, Object> capas = (Map<String, Object>) properties.get("capabilities");

        //2. Parse input params
        host = getPropertyValue(host, settings, "host", DEFAULT_HOST_VALUE);
        if (!host.equals(DEFAULT_HOST_VALUE)) {
            platform = getPropertyValue(platform, settings, "platform", "");
            if (platform.isEmpty()) {
                throw new Exception("Platform name should be specified");
            }
        } else {
            platform = getPlatformName();
        }

        System.setProperty(HOST_ENV_VAR, host);

        browser = getPropertyValue(browser, settings, "browser", "chrome");
        buildVersion = getPropertyValue(buildVersion, settings, "build", "");

        timeout = getPropertyValue(timeout, settings, "timeout", "30");
        if (timeout == null || timeout.isEmpty()) {
            IMPLICIT_WAIT_TIME_OUT = 30;
        } else {
            IMPLICIT_WAIT_TIME_OUT = Integer.parseInt(timeout);
        }

        Log.info("Browser type requested: " + browser + "; Operating System: " + platform);

        BUILD = buildVersion;
        PLATFORM = platform;
        BROWSER_PROFILE = browser;

        //3. get specifically properties according to the platform/browser
        Map<String, Object> capsFromFile = (Map<String, Object>) capas.get(platform);
        capsFromFile = (Map<String, Object>) capsFromFile.get(browser);

        //4. Create capabilities based on input params
        MutableCapabilities capabilities =
                getCapabilities(capsFromFile, platform, browser,
                        getPropVal(settings, "proxy"));

        DriverFactory.capabilities = capabilities;

        // Set local driver version
        try {
            LOCAL_DRIVER_VERSION = capabilities.getCapability("localDriverVersion").toString();
        } catch (NullPointerException e) {
            // Nothing to catch
        }

        //5. Setup Listener
        String platformVersion = getPropVal(capsFromFile, PLATFORM_VERSION_KEY);
        String browserVersion = getPropVal(capsFromFile, BROWSER_VERSION_KEY);

        System.setProperty("mplatformfullname", platformVersion);
        System.setProperty("mbrowserfullname", browser + " " + browserVersion);

        //6. Override capabilities by VM options
        String _app = System.getProperty("mApp");
        String _platformVersion = System.getProperty("mPlatformVersion");
        String _deviceName = System.getProperty("mDeviceName");
        if (Strings.isNotNullAndNotEmpty(_app))
            capabilities.setCapability("app", _app);
        if (Strings.isNotNullAndNotEmpty(_platformVersion))
            capabilities.setCapability("platformVersion", _platformVersion);
        if (Strings.isNotNullAndNotEmpty(_deviceName))
            capabilities.setCapability("deviceName", _deviceName);
        //7. Create Driver
        return createDriver(capabilities, host, browser);
    }

    private static RemoteWebDriver createDriver(MutableCapabilities options, String host, String browser) throws Exception {
        RemoteWebDriver driver;
        if (host.equals(DEFAULT_HOST_VALUE)) {
            switch (processBrowserName(browser)) {
                case "firefox":
                    synchronized (DriverFactory.class) {
                        WebDriverManager.firefoxdriver().version(LOCAL_DRIVER_VERSION).setup();
                    }
                    driver = new FirefoxDriver((FirefoxOptions) options);
                    break;
                case "chrome":
                    synchronized (DriverFactory.class) {
                        String proxyServer = System.getProperty("proxy");
                        String proxyUser = System.getProperty("proxyUser");
                        String proxyPass = System.getProperty("proxyPass");
                        if (!Strings.isNullOrEmpty(proxyServer)) {
                            WebDriverManager.chromedriver().version(LOCAL_DRIVER_VERSION).proxy(proxyServer)
                                    .proxyUser(proxyUser)
                                    .proxyPass(proxyPass)
                                    .setup();
                        } else {
                            WebDriverManager.chromedriver().version(LOCAL_DRIVER_VERSION).setup();
                        }
                    }
                    driver = new ChromeDriver((ChromeOptions) options);
                    break;
                case "ie":
                    synchronized (DriverFactory.class) {
                        WebDriverManager.iedriver().version(LOCAL_DRIVER_VERSION).setup();
                    }
                    driver = new InternetExplorerDriver((InternetExplorerOptions) options);
                    break;
                case "edge":
                    synchronized (DriverFactory.class) {
                        WebDriverManager.edgedriver().version(LOCAL_DRIVER_VERSION).setup();
                    }
                    driver = new EdgeDriver((EdgeOptions) options);
                    break;
                case "safari":
                    driver = new SafariDriver((SafariOptions) options);
                    break;
                default:
                    throw new Exception("Unsupported browser: " + browser);
            }
        } else {
            String REMOTE_ADDRESS = String.format("%s/wd/hub", host);

            switch (processBrowserName(browser)) {
                case IOS_PROFILE:
                    driver = new IOSDriver<>(new URL(REMOTE_ADDRESS), options);
                    break;
                case ANDROID_BROWSER_PROFILE:
                case ANDROID_APP_PROFILE:
                    //options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                    //options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
                    // TODO - KoCTblJlb
                    if (options.getCapability("browserName") != null &&
                            options.getCapability("app") != null) {
                        String app = options.getCapability("app").toString();
                        options.setCapability("app", "");
                        driver = new AndroidDriver<>(new URL(REMOTE_ADDRESS), options);
                        driver.quit();
                        options.setCapability("app", app);
                        options.setCapability("browserName", "");
                    }
                    driver = new AndroidDriver<>(new URL(REMOTE_ADDRESS), options);
                    break;
                default:
                    driver = new RemoteWebDriver(new URL(REMOTE_ADDRESS), options);
                    break;
            }
        }
        driver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT_TIME_OUT, TimeUnit.SECONDS);
        return driver;
    }

    public static MutableCapabilities getCapabilities() {
        return capabilities;
    }

    private static MutableCapabilities getCapabilities(Map<String, Object> capsFromFile,
                                                       String platform,
                                                       String browser,
                                                       String proxy) {

        capsFromFile = parseCaps(capsFromFile);

        MutableCapabilities capabilities = getCapabilities(platform, browser);

        if (!Strings.isNullOrEmpty(proxy)) {
            Proxy proxy_caps = new Proxy();
            proxy_caps.setHttpProxy(proxy);
            proxy_caps.setSslProxy(proxy);

            capabilities.setCapability("proxy", proxy_caps);
        }
        addCapsFromFile(capabilities, capsFromFile);
        return capabilities;
    }

    private static Map<String, Object> parseCaps(Map<String, Object> caps) {
        return caps
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(
                                e -> e.getKey(),
                                e -> e.getValue())
                );
    }

    private static MutableCapabilities getCapabilities(String platform, String browser) {
        MutableCapabilities capabilities;

        switch (processBrowserName(browser)) {
            case DESKTOP_FIREFOX_PROFILE:
                capabilities = new FirefoxOptions();
                break;
            case DESKTOP_CHROME_PROFILE:
            case ANDROID_BROWSER_PROFILE:
                capabilities = new ChromeOptions();
                break;
            case ANDROID_APP_PROFILE:
                capabilities = DesiredCapabilities.android();
                break;
            case DESKTOP_EDGE_PROFILE:
                capabilities = new EdgeOptions();
                break;
            case DESKTOP_IE_PROFILE:
                capabilities = new InternetExplorerOptions();
                break;
            case DESKTOP_SAFARI_PROFILE:
            case IOS_PROFILE:
                capabilities = new SafariOptions();
                break;
            default:
                throw new RuntimeException("Unknown platform = " + platform + " for Browser = " + browser);
        }

        return capabilities;
    }

    private static void addCapsFromFile(MutableCapabilities caps, Map<String, Object> capsFromFile) {
        capsFromFile.forEach((capsKey, capsVal) -> {
            String key = String.valueOf(capsKey);
            switch (key) {
                case "args":
                    switch (BROWSER_PROFILE) {
                        case DESKTOP_CHROME_PROFILE:
                        case EMULATOR_MOBILE_CHROME_PROFILE:
                            try {
                                ((ChromeOptions) caps).addArguments((List) capsVal);
                            } catch (ClassCastException err) {
                                Log.error("Invalid chrome arguments: " + err.getMessage());
                            }
                            break;
                        case DESKTOP_FIREFOX_PROFILE:
                            try {
                                ((FirefoxOptions) caps).addArguments((List) capsVal);
                            } catch (ClassCastException err) {
                                Log.error("Invalid firefox arguments: " + err.getMessage());
                            }
                            break;
                    }
                    break;
                case "experimentalOptions":
                    switch (BROWSER_PROFILE) {
                        case DESKTOP_CHROME_PROFILE:
                        case EMULATOR_MOBILE_CHROME_PROFILE:
                            try {
                                ((Map) capsVal).forEach((expKey, expVal) -> {
                                    ((ChromeOptions) caps).setExperimentalOption(expKey.toString(), expVal);
                                });
                            } catch (Exception err) {
                                Log.error("Invalid experimentalOptions: " + err.getMessage());
                            }
                            break;
                    }
                    break;
                default:
                    caps.setCapability(key, capsVal);
                    break;
            }
        });
    }

    private static String getPropVal(Map properties, String key) {
        Object prop = properties.get(key);

        if (prop != null) {
            if (prop instanceof String) {
                return (String) prop;
            } else {
                return String.valueOf(prop);
            }
        }

        return "";
    }

    private static String getPlatformName() {
        String platform = System.getProperty("os.name").toLowerCase();
        if (platform.contains("win")) {
            platform = "windows";
        } else if (platform.contains("mac")) {
            platform = "mac";
        } else if (platform.contains("linux")) {
            platform = "linux";
        }
        return platform;
    }

    public static Map loadPropertiesFromFile(String pathToFile) {
        Map res = null;
        try (FileInputStream input = new FileInputStream(pathToFile)) {
            Yaml yaml = new Yaml();
            res = yaml.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String getPropertyValue(String propertyName, Map<String, Object> settings, String settingKey, String defaultValue) {
        if (propertyName == null || propertyName.isEmpty()) {
            propertyName = getPropVal(settings, settingKey);
            if (propertyName == null || propertyName.isEmpty()) {
                Log.info(String.format("Property '%s' set to the default value: %s", settingKey, defaultValue));
                return defaultValue;
            } else {
                Log.info(String.format("Property '%s' was init from file, value: %s", settingKey, propertyName));
            }
        } else {
            Log.info(String.format("Property '%s' was init from param, value: %s", settingKey, propertyName));
        }

        return propertyName;
    }

    /**
     * Collapse all specific browsers versions to the common
     *
     * @param browser
     * @return
     */
    private static String processBrowserName(String browser) {
        if (browser.toLowerCase().contains("chrome")) {
            return DESKTOP_CHROME_PROFILE;
        } else if (browser.toLowerCase().contains("firefox")) {
            return DESKTOP_FIREFOX_PROFILE;
        } else if (browser.toLowerCase().contains("android_browser")) {
            return ANDROID_BROWSER_PROFILE;
        } else if (browser.toLowerCase().contains("android_app")) {
            return ANDROID_APP_PROFILE;
        } else if (browser.toLowerCase().contains("ios")) {
            return IOS_PROFILE;
        }
        return browser;
    }

    /**
     * Get a path to the default download directory
     *
     * @return
     */
    public static String getDefaultDownloadDirectory() {
        if ("chrome".equals(BROWSER_PROFILE)) {
            String path = null;
            Map<String, String> prefsMap = (Map<String, String>) ((Map<String, Object>) capabilities.asMap().
                    get("goog:chromeOptions")).get("prefs");
            if (prefsMap != null)
                path = prefsMap.get("download.default_directory");
            if (Strings.isNotNullAndNotEmpty(path))
                return path;
            else
                return System.getProperty("user.home") + "\\Downloads";
            /*return ((Map<String, String>) ((ChromeOptions)capabilities).getExperimentalOption("prefs")).
            get("download.default_directory");*/
        } else
            throw new RuntimeException("Method DriverFactory.getDefaultDownloadDirectory() " +
                    "should be used for Chrome only");
    }

    public static boolean isAppProfile() {
        return BROWSER_PROFILE.contains("app");
    }
}
