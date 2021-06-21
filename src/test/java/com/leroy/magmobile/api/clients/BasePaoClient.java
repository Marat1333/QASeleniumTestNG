package com.leroy.magmobile.api.clients;

import com.leroy.constants.EnvConstants;
import com.leroy.core.api.BaseMashupClient;

public abstract class BasePaoClient extends BaseMashupClient {

    @Override
    protected void init() {
        gatewayUrl = EnvConstants.PAO_API_HOST;
        jaegerHost = EnvConstants.PAO_JAEGER_HOST;
        jaegerService = EnvConstants.PAO_JAEGER_SERVICE;
    }
}
