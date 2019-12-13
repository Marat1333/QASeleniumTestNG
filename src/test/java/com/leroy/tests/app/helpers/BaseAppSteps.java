package com.leroy.tests.app.helpers;

import com.leroy.constants.EnvConstants;
import com.leroy.models.UserData;
import com.leroy.pages.LoginPage;
import com.leroy.pages.app.SalesPage;
import com.leroy.pages.app.UserProfilePage;
import com.leroy.pages.app.common.BottomMenuPage;
import com.leroy.tests.BaseState;

public class BaseAppSteps extends BaseState {

    // Main sections of Mag mobile app
    protected final String SALES_SECTION = "Продажа";
    protected final String WORK_SECTION = "Работа";
    protected final String SUPPORT_SECTION = "Поддержка";
    protected final String MORE_SECTION = "еще";

    // Sub sections for the SALES_SECTION
    protected final String DOCUMENTS_SALES_SECTION = "Документы продажи";

    // ------------------- STEPS ---------------------//
    protected void loginInAndGoTo(UserData userData, String section) {
        LoginPage loginPage = new LoginPage(driver);
        SalesPage salesPage = loginPage.logIn(userData);
        switch (section) {
            case SALES_SECTION:
                // Nothing to do because it is default page after login
                break;
            case DOCUMENTS_SALES_SECTION:

                break;
            case WORK_SECTION:
                salesPage.goToWork();
                break;
            case SUPPORT_SECTION:
                salesPage.goToSupport();
                break;
            case MORE_SECTION:
                salesPage.goToMoreSection();
                break;
        }
    }

    protected void loginInAndGoTo(String section) {
        loginInAndGoTo(new UserData(EnvConstants.BASIC_USER_NAME, EnvConstants.BASIC_USER_PASS), section);
    }

    protected UserProfilePage setShopAndDepartmentForUser(String shop, String department) {
        BottomMenuPage bottomMenuPage = new BottomMenuPage(driver);
        return bottomMenuPage.goToMoreSection().goToUserProfile().goToEditShopForm().searchForShopAndSelectById(shop)
                .goToEditDepartmentForm().selectDepartmentById(department);
    }

}
