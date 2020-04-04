package com.leroy.magmobile.api.tests.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.StatusCodes;
import com.leroy.magmobile.api.clients.Is4AuthClient;
import com.leroy.magmobile.api.data.oauth.Is4TokenData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import com.leroy.umbrella_extension.authorization.AuthClient;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class AuthorizationTest extends BaseProjectApiTest {

    @Inject
    private Provider<AuthClient> authClientProvider;

    @Inject
    private Provider<Is4AuthClient> is4AuthClientProvider;

    @Test(description = "Authorization with valid credentials")
    public void testAuthorizationWithValidCredentials() {
        String code = authClientProvider.get().authAndGetCode(EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS);
        Response<Is4TokenData> response = is4AuthClientProvider.get().sendPostCodeRequest(code);
        assertThat(response, successful());
        Is4TokenData tokenData = response.asJson();
        assertThat("access_token", tokenData.getAccessToken(), not(emptyOrNullString()));
        assertThat("expires_in", tokenData.getExpiresIn(), greaterThan(1));
        assertThat("refresh token", tokenData.getRefreshToken(), not(emptyOrNullString()));
        assertThat("ldap", tokenData.getLdap(), is(EnvConstants.BASIC_USER_LDAP));
        assertThat("name", tokenData.getName(), not(emptyOrNullString()));
        assertThat("surname", tokenData.getSurname(), not(emptyOrNullString()));
        assertThat("title", tokenData.getTitle(), not(emptyOrNullString()));
        assertThat("userEmail", tokenData.getUserEmail(), not(emptyOrNullString()));
        assertThat("titleEmail", tokenData.getTitleEmail(), not(emptyOrNullString()));
        assertThat("shopId", tokenData.getShopId(), is(Integer.parseInt(EnvConstants.BASIC_USER_SHOP_ID)));
        assertThat("shopName", tokenData.getShopName(), not(emptyOrNullString()));
        assertThat("department id", tokenData.getDepartmentId(), is(Integer.parseInt(EnvConstants.BASIC_USER_DEPARTMENT_ID)));
        assertThat("department Name", tokenData.getDepartmentName(), not(emptyOrNullString()));
        assertThat("store currency", tokenData.getStoreCurrency(), is("RUR"));
        assertThat("grants", tokenData.getGrants(), notNullValue());
        assertThat("roles", tokenData.getUserRoles(), notNullValue());
        assertThat("shop region", tokenData.getShopRegion(), notNullValue());
    }

    @Test(description = "Authorization with invalid code")
    public void testAuthorizationWithInvalidCredentials() {
        Response<Is4TokenData> response = is4AuthClientProvider.get()
                .sendPostCodeRequest("a2e508c1-bdbd-4d4d-8a5d-d88155812f64");
        assertThat("response status", response.getStatusCode(), is(StatusCodes.ST_400_NOT_AUTH));
        JsonNode jsonNode = response.asJson(JsonNode.class);
        assertThat("error text", jsonNode.get("error").asText(), is("invalid_grant"));
    }

}
