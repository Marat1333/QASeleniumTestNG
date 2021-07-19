package com.leroy.magmobile.api.tests.auth;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.api.StatusCodes;
import com.leroy.magmobile.api.clients.Is4AuthClient;
import com.leroy.magmobile.api.data.oauth.Is4TokenData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import com.leroy.umbrella_extension.authorization.AuthClient;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;
import org.testng.util.Strings;
import ru.leroymerlin.qa.core.clients.base.Response;


public class AuthorizationTest extends BaseProjectApiTest {

    @Inject
    private AuthClient authClient;

    @Inject
    private Is4AuthClient is4AuthClient;

    private Is4TokenData tokenData;

    @Test(description = "C3137640 Authorization with valid credentials")
    @TmsLink("3048")
    public void testAuthorizationWithValidCredentials() {
        String code = authClient.authAndGetCode(EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS);
        Response<Is4TokenData> response = is4AuthClient.sendPostCodeRequest(code);
        assertThat(response, successful());
        tokenData = response.asJson();
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(tokenData.getAccessToken()), "access_token");
        softAssert().isTrue( tokenData.getExpiresIn() > 1, "expires_in");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(tokenData.getRefreshToken()), "refresh token");
        softAssert().isTrue(tokenData.getLdap().equalsIgnoreCase(EnvConstants.BASIC_USER_LDAP), "ldap");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(tokenData.getName()), "name");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(tokenData.getSurname()), "surname");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(tokenData.getTitle()), "title");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(tokenData.getUserEmail()), "userEmail");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(tokenData.getTitleEmail()), "titleEmail");
        softAssert().isTrue(tokenData.getShopId().equals(Integer.parseInt(EnvConstants.BASIC_USER_SHOP_ID)), "shopId");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(tokenData.getShopName()), "shopName");
        softAssert().isTrue(tokenData.getDepartmentId().equals(Integer.parseInt(EnvConstants.BASIC_USER_DEPARTMENT_ID)), "department id");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(tokenData.getDepartmentName()), "department Name");
        softAssert().isTrue(tokenData.getStoreCurrency().equals("RUR"), "store currency");
        softAssert().isTrue(tokenData.getGrants() != null, "grants");
        softAssert().isTrue(tokenData.getUserRoles() != null, "roles");
        softAssert().isTrue(tokenData.getShopRegion() != null, "shop region");
        softAssert().verifyAll();
    }

    @Test(description = "C3159000 Authorization with invalid code")
    @TmsLink("3049")
    public void testAuthorizationWithInvalidCode() {
        Response<Is4TokenData> response = is4AuthClient
                .sendPostCodeRequest("a2e508c1-bdbd-4d4d-8a5d-d88155812f64");
        assertThat("response status", response.getStatusCode(),
                is(StatusCodes.ST_400_BAD_REQ));
        JsonNode jsonNode = response.asJson(JsonNode.class);
        assertThat("error text", jsonNode.get("error").asText(), is("invalid_grant"));
    }

    @Test(description = "C3255565 Refresh token - happy path")
    @TmsLink("3050")
    public void testRefreshTokenHappyPath() {
        if (tokenData == null)
            throw new IllegalArgumentException("Token hasn't been created");
        Response<Is4TokenData> response = is4AuthClient.sendPostRefreshRequest(tokenData.getRefreshToken());
        assertThat(response, successful());
        Is4TokenData tokenData = response.asJson();
        assertThat("access_token", tokenData.getAccessToken(), not(emptyOrNullString()));
        assertThat("expires_in", tokenData.getExpiresIn(), greaterThan(1));
        assertThat("refresh token", tokenData.getRefreshToken(), not(emptyOrNullString()));
    }

    @Test(description = "C3255566 Refresh with invalid token")
    @TmsLink("3051")
    public void testRefreshInvalidToken() {
        Response<Is4TokenData> response = is4AuthClient
                .sendPostRefreshRequest("a2e508c1-bdbd-4d4d-8a5d-d88155812f64");
        assertThat("response status", response.getStatusCode(),
                is(StatusCodes.ST_400_BAD_REQ));
        JsonNode jsonNode = response.asJson(JsonNode.class);
        assertThat("error text", jsonNode.get("error").asText(), is("invalid_grant"));
    }

}
