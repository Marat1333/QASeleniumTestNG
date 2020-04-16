package com.leroy.umbrella_extension.authorization.requests;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;

@Method(value = "GET", path = "/connect/authorize/callback")
public class AuthorizeCallbackGetRequest extends RequestBuilder<AuthorizeCallbackGetRequest> {

}
