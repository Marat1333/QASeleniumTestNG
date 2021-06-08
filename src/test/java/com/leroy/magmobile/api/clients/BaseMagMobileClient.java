package com.leroy.magmobile.api.clients;

import com.leroy.constants.EnvConstants;
import com.leroy.core.api.BaseMashupClient;

public abstract class BaseMagMobileClient extends BaseMashupClient {

    @Override
    protected void init() {
        gatewayUrl = EnvConstants.MAGMOBILE_API_HOST;
        jaegerHost = EnvConstants.MAGMOBILE_JAEGER_HOST;
        jaegerService = EnvConstants.MAGMOBILE_JAEGER_SERVICE;
    }
}
