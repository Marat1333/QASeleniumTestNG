package com.leroy.constants;

import com.leroy.core.configuration.Log;
import org.testng.annotations.Test;
import org.testng.util.Strings;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class EnvConstants {

    private static final String PRODUCTION_ENV = "dev";
    private static final String ENV = getEnvironment();
    private static final Properties properties = getPropertiesForEnv(ENV);

    // User options
    public static final String BASIC_USER_NAME = getProperty("basic.user.name");
    public static final String BASIC_USER_PASS = getProperty("basic.user.password");
    public static final String BASIC_USER_SHOP_ID = getProperty("basic.user.shopId");

    // URLs
    private static String getUrlMagPortal() {
        String branchForTesting = System.getProperty("testBranch");
        if (branchForTesting != null)
            return String.format("https://%s.prudevlegowp.hq.ru.corp.leroymerlin.com", branchForTesting.toLowerCase().replaceAll("/","-"));
        return System.getProperty("portalUrl", getProperty("url.mag.portal"));
    }
    public static final String URL_MAG_PORTAL = getUrlMagPortal();

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
                    + String.format("/src/main/resources/propertyFiles/%s.properties", env));
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