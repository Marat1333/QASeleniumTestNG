package com.leroy.magportal.api.helpers;

import com.leroy.core.ContextProvider;
import com.leroy.core.UserSessionData;

public class BaseHelper {

    protected UserSessionData userSessionData() {
        return ContextProvider.getContext().getUserSessionData();
    }

}
