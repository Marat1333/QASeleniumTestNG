package com.leroy.constants;

import com.leroy.core.configuration.Log;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.testng.util.Strings;
import ru.leroymerlin.qa.core.commons.enums.Environment;
import ru.leroymerlin.qa.core.config.client.EnvironmentConfig;

public class EnvConstants {

    public static final String ENV = getEnvironment();
    private static final Map properties = getProperties();

    // Authorization
    public static final String IS4_AUTH_HOST = getProperty("lego.auth.host");
    public static final String AUTH_CLIENT_ID = getProperty("lego.auth.clientId");
    public static final String MM_AUTH_CLIENT_ID = getProperty("magmobile.auth.clientId");
    public static final String MP_AUTH_CLIENT_ID = getProperty("magportal.auth.clientId");
    public static final String AUTH_SECRET_KEY = getProperty("lego.auth.secretKey");

    // User options
    public static final String BASIC_USER_LDAP = getProperty("lego.user.ldap");
    public static final String BASIC_USER_PASS = getProperty("lego.user.password");
    public static final String BASIC_USER_FIRST_NAME = getProperty("lego.user.name");
    public static final String BASIC_USER_SHOP_ID = getProperty("lego.user.shopId");
    public static final String BASIC_USER_DEPARTMENT_ID = getProperty("lego.user.departmentId");
    public static final String RABBIT_USER_NAME = getProperty("rabbit.username");
    public static final String RABBIT_USER_PASS = getProperty("rabbit.password");

    // URLs
    private static String getUrlMagPortal() {
        String branchForTesting = System.getProperty("testBranch");
        String portalUrl = getProperty("magportal.ui.host");
        if (branchForTesting != null)
            return portalUrl.replace("dev", branchForTesting.toLowerCase().replaceAll("/", "-"))
                            .replace("preprod", branchForTesting.toLowerCase().replaceAll("/", "-"));
        return portalUrl;
    }

    public static final String URL_MAG_PORTAL = getUrlMagPortal();

    // Test data
    // Shops
    public static final String SHOP_WITH_NEW_INTERFACE = getProperty("lego.shop.new_interface");
    public static final String SHOP_WITH_OLD_INTERFACE = getProperty("lego.shop.old_interface");

    // Services
    public static final String SERVICE_1_LM_CODE = getProperty("lego.service1.lmCode");

    // -------------- API HOSTS --------------- //
    public static final String MAGPORTAL_API_HOST = getProperty("magportal.api.host");
    public static final String MAGMOBILE_API_HOST = getProperty("magmobile.api.host");
    public static final String PRODUCTSEARCH_API_HOST = getProperty("productsearch.api.host");
    public static final String CLIENTS_API_HOST = getProperty("clients.api.host");
    public static final String RUPTURES_API_HOST = getProperty("ruptures.api.host");
    public static final String PICK_API_HOST = getProperty("pick.api.host");
    public static final String PAO_API_HOST = getProperty("pao.api.host");
    public static final String SHOPS_API_HOST = getProperty("shops.api.host");
    public static final String RABBIT_UI_HOST = getProperty("rabbit.ui.host");

    // Jaeger
    public static final String MAGPORTAL_JAEGER_HOST = getProperty("magportal.jaeger.host");
    public static final String MAGPORTAL_JAEGER_SERVICE = getProperty("magportal.jaeger.service");
    public static final String MAGMOBILE_JAEGER_HOST = getProperty("magmobile.jaeger.host");
    public static final String MAGMOBILE_JAEGER_SERVICE = getProperty("magmobile.jaeger.service");
    public static final String RUPTURES_JAEGER_HOST = getProperty("ruptures.jaeger.host");
    public static final String RUPTURES_JAEGER_SERVICE = getProperty("ruptures.jaeger.service");
    public static final String PRODUCTSEARCH_JAEGER_HOST = getProperty("productsearch.jaeger.host");
    public static final String PRODUCTSEARCH_JAEGER_SERVICE = getProperty("productsearch.jaeger.service");
    public static final String PICK_JAEGER_HOST = getProperty("pick.jaeger.host");
    public static final String PICK_JAEGER_SERVICE = getProperty("pick.jaeger.service");
    public static final String PAO_JAEGER_HOST = getProperty("pao.jaeger.host");
    public static final String PAO_JAEGER_SERVICE = getProperty("pao.jaeger.service");
    public static final String CLIENTS_JAEGER_HOST = getProperty("clients.jaeger.host");
    public static final String CLIENTS_JAEGER_SERVICE = getProperty("clients.jaeger.service");

    /**
     * Get the value of a property key
     *
     * @param key
     * @return
     */
    protected static String getProperty(String key) {
        return (String) properties.get(key);
    }

    private static Map getProperties() {
        String env = getEnvironment();
        Log.info("Running tests in environment: " + env + "\n");
        Properties localProperties = new Properties();
        if (env.contains("anybranch") || env.contains("internal")) {
            try {
                FileReader reader = new FileReader(System.getProperty("user.dir")
                        + String.format("/src/main/resources/propertyFiles/%s.properties", env));
                localProperties.load(reader);

            } catch (IOException e) {
                Log.error(e.getMessage());
            }
        } else {
            return EnvironmentConfig.get(Environment.getEnvironment(env)).getProperties();
        }

        return localProperties;
    }

    private static String getEnvironment() {
        return Strings.isNullOrEmpty(System.getProperty("env")) ? "test" : System.getProperty("env").toLowerCase();
    }
}