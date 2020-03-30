package com.leroy.magmobile.api.tests.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.constants.StatusCodes;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import com.leroy.umbrella_extension.authorization.AuthClient;
import com.leroy.umbrella_extension.authorization.data.TokenData;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class AuthorizationTest extends BaseProjectApiTest {

    @Inject
    private AuthClient authClient;

    @Test(description = "Authorization with valid credentials")
    public void testAuthorizationWithValidCredentials() {
        Response<TokenData> response = authClient.getResponseToken(
                EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS);
        assertThat(response, successful());
        TokenData tokenData = response.asJson();
        assertThat("access_token", tokenData.getAccess_token(), not(emptyOrNullString()));
        assertThat("expires_in", tokenData.getExpires_in(), greaterThan(1));
        assertThat("token type", tokenData.getToken_type(), equalTo("Bearer"));
        assertThat("refresh token", tokenData.getRefresh_token(), not(emptyOrNullString()));
        assertThat("scope", tokenData.getScope(), not(emptyOrNullString()));
    }

    @Test(description = "Authorization with invalid credentials")
    public void testAuthorizationWithInvalidCredentials() {
        Response<TokenData> response = authClient.getResponseToken(
                RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(8));
        assertThat("response status", response.getStatusCode(), is(StatusCodes.ST_400_NOT_AUTH));
        JsonNode jsonNode = response.asJson(JsonNode.class);
        assertThat("error text", jsonNode.get("error").asText(), is("invalid_grant"));
        assertThat("error description", jsonNode.get("error_description").asText(),
                is("invalid_username_or_password"));
    }

}
