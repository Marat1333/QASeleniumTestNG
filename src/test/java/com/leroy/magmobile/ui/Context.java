package com.leroy.magmobile.ui;

import com.leroy.core.UserSessionData;
import com.leroy.core.asserts.AssertWrapper;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.core.testrail.helpers.StepLog;
import lombok.Data;

@Data
public class Context {

    private SoftAssertWrapper softAssert;
    private AssertWrapper anAssert;
    private String tcId;
    private StepLog log;
    private UserSessionData userSessionData;

    // TODO Remove
    public boolean isNewShopFunctionality() {
        return userSessionData.getUserShopId().equals("35");
    }
}
