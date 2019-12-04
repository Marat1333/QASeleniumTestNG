package com.leroy.constants;

import com.leroy.core.configuration.Log;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class EnvConstants {

    private static final String PRODUCTION_ENV = "prod";
    private static final String ENV = getEnvironment();
    private static final Properties properties = getPropertiesForEnv(ENV);

    //URL params
    public static final String URL_PARAM_HIDE_NAVBAR = "hideNavBar=1";

    //URLs
    public static final String URL_SCHEME = "true".equals(System.getProperty("fakeRouter")) ? "http://" : getProperty("url.scheme");

    public static final String USERDOMEN = getProperty("url.userdomen");
    public static final String USERNAME = getProperty("url.username");
    public static final String URL_PASSWORD = getProperty("url.password");
    public static final String URL_USER_INFO = USERNAME.isEmpty() ? "" : USERNAME + ":" + URL_PASSWORD + "@";

    public static final String URL_HOST = Strings.isNotNullAndNotEmpty(System.getProperty("hostRepos"))?
            System.getProperty("hostRepos") : getProperty("url.host");
    public static final String URL_PORT = getProperty("url.port");

    public static final String URL_BASE_PART_WITH_USER_INFO = URL_SCHEME + URL_USER_INFO + URL_HOST + URL_PORT;
    public static final String URL_BASE_PART_WITHOUT_USER_INFO = URL_SCHEME + URL_HOST + URL_PORT;
    public static final String URL_HOME_PAGE_WITH_XHQ_PREFIX = URL_BASE_PART_WITHOUT_USER_INFO + "/xhq";
    public static final String URL_SOLUTION_VIEWER =
            URL_BASE_PART_WITHOUT_USER_INFO + getProperty("url.solution.viewer.path") + "/";
    public static final String URL_SOLUTION_VIEWER_WITHOUT_USER_INFO =
            URL_BASE_PART_WITHOUT_USER_INFO + getProperty("url.solution.viewer.path") + "/" +
                    ("true".equals(System.getProperty("perflog")) ? "?perflog=true" : "") +
                    ("true".equals(System.getProperty("fakeRouter")) ? "" : "");
    public static final String URL_SOLUTION_VIEWER_WITHOUT_USER_INFO_HIDE_NAVBAR =
            URL_SOLUTION_VIEWER_WITHOUT_USER_INFO + "?" + URL_PARAM_HIDE_NAVBAR;

    //Pages URLs
    public static final String HOME_PAGE_URL = URL_SOLUTION_VIEWER + getProperty("url.home.page.fragment");
    public static final String HOME_PAGE_URL_WITH_USER_AND_SID = URL_BASE_PART_WITHOUT_USER_INFO +
            getProperty("url.solution.viewer.path") + "?userName=" + getProperty("url.userdomen") + "%5c" +
            getProperty("url.username") + "&userSID=" + getProperty("url.usersid");
    public static final String USER_SETTINGS_PAGE_URL =
            URL_SOLUTION_VIEWER_WITHOUT_USER_INFO + getProperty("url.users.settings.page.path");
    public static final String ALERTS_PAGE_URL = URL_BASE_PART_WITHOUT_USER_INFO + getProperty("url.alerts.page.path");
    public static final String HELP_PAGE_URL = URL_SCHEME + URL_HOST + URL_PORT + getProperty("url.help.page.path");

    @Test(description = "Get constant")
    public void getConstant() throws Exception {
        Log.info("Constant = " + URL_SOLUTION_VIEWER_WITHOUT_USER_INFO);
    }

    /**
     * Get the value of a property key
     *
     * @param key
     * @return
     */
    protected static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Return the Properties object from environment xml file
     *
     * @param env
     * @return
     */
    private static Properties getPropertiesForEnv(String env) {
        Properties properties = null;
        try {
            properties = new Properties();
            FileReader reader = new FileReader(System.getProperty("user.dir")
                    + String.format("/src/main/resources/PropertyFiles/%s.properties", env));
            properties.load(reader);

        } catch (IOException e) {

        }
        return properties;
    }

    /**
     * Get the environment out of Maven, or default to prod
     *
     * @return
     */
    private static String getEnvironment() {
        String env = PRODUCTION_ENV;
        String mavenEnv = System.getProperty("menv");
        if (!Strings.isNullOrEmpty(mavenEnv)) {
            env = mavenEnv;
        }
        Log.info("Running test in environment: " + env + "\n");
        return env;
    }
}