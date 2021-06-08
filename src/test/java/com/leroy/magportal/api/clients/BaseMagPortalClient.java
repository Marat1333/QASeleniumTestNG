package com.leroy.magportal.api.clients;

import com.leroy.constants.EnvConstants;
import com.leroy.core.api.BaseMashupClient;

public abstract class BaseMagPortalClient extends BaseMashupClient {

    @Override
    protected void init() {
        gatewayUrl = EnvConstants.MAGPORTAL_API_HOST;
        jaegerHost = EnvConstants.MAGPORTAL_JAEGER_HOST;
        jaegerService = EnvConstants.MAGPORTAL_JAEGER_SERVICE;
    }
}
