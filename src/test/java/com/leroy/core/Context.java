package com.leroy.core;

import com.leroy.core.asserts.AssertWrapper;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.core.testrail.helpers.StepLog;
import lombok.Data;

import java.util.Arrays;

@Data
public class Context {

    private SoftAssertWrapper softAssert;
    private AssertWrapper anAssert;
    private String tcId;
    private StepLog log;
    private UserSessionData userSessionData;

    String[] oldShops = {"78", "6"};

    public boolean isNewShopFunctionality() {
        return !Arrays.asList(oldShops).contains(userSessionData.getUserShopId());
    }
}
