package com.leroy.constants;

import com.leroy.core.configuration.Log;
import org.testng.util.Strings;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class EnvConstants {

    private static final String PRODUCTION_ENV = "dev";
    private static final String ENV = getEnvironment();
    private static final Properties properties = getPropertiesForEnv(ENV);

    // Authorization
    public static final String IS4_AUTH_HOST = getProperty("url.is4.auth.host");
    public static final String AUTH_CLIENT_ID = getProperty("auth.clientId");
    public static final String AUTH_SECRET_KEY = getProperty("auth.secretKey");

    // User options
    public static final String BASIC_USER_LDAP = getProperty("basic.user.ldap");
    public static final String BASIC_USER_PASS = getProperty("basic.user.password");
    public static final String BASIC_USER_FIRST_NAME = getProperty("basic.user.name");
    public static final String BASIC_USER_SHOP_ID = getProperty("basic.user.shopId");
    public static final String BASIC_USER_DEPARTMENT_ID = getProperty("basic.user.departmentId");
    public static final String RABBIT_USER_NAME = getProperty("rabbit.user.name");
    public static final String RABBIT_USER_PASS = getProperty("rabbit.user.password");

    // Backend environment
    public static final String BACKEND_CLIENT_ENV = getProperty("backend.environment");

    // URLs
    private static String getUrlMagPortal() {
        String branchForTesting = System.getProperty("testBranch");
        if (branchForTesting != null)
            return String.format("https://%s.prudevlegowp.hq.ru.corp.leroymerlin.com", branchForTesting.toLowerCase().replaceAll("/", "-"));
        return System.getProperty("portalUrl", getProperty("url.ui.main"));
    }

    public static final String URL_MAG_PORTAL = getUrlMagPortal();
    public static final String URL_MAG_PORTAL_OLD = getProperty("url.ui.old");

    // Test data
    // Shops
    public static final String SHOP_WITH_NEW_INTERFACE = getProperty("data.shop.new_interface");
    public static final String SHOP_WITH_OLD_INTERFACE = getProperty("data.shop.old_interface");

    // Services
    public static final String SERVICE_1_LM_CODE = getProperty("data.service1.lmCode");

    // -------------- API HOSTS --------------- //
    public static final String MAIN_API_HOST = getProperty("url.api.main_host");
    public static final String SEARCH_API_HOST = getProperty("url.api.search_host");
    public static final String CLIENT_API_HOST = getProperty("url.api.client_host");
    public static final String RUPTURES_API_HOST = getProperty("url.api.ruptures_host");
    public static final String PICK_API_HOST = getProperty("url.api.pick_host");
    public static final String RABBIT_API_HOST = getProperty("url.api.rabbit.host");

    // Jaeger
    public static final String JAEGER_HOST = getProperty("url.jaeger.host");
    public static final String JAEGER_SERVICE = getProperty("url.jaeger.service");

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
            Log.error(e.getMessage());
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
        String mavenEnv = System.getProperty("env");
        if (!Strings.isNullOrEmpty(mavenEnv)) {
            env = mavenEnv;
        }
        Log.info("Running test in environment: " + env + "\n");
        return env;
    }
}