package com.leroy.magmobile.api.tests.ruptures;

import com.fasterxml.jackson.databind.JsonNode;
import com.leroy.magmobile.api.enums.CorrectionAccessLevels;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class RupturesStockCorrectionAccessCheckTests extends BaseRuptureTest{

    @Test(description = "C23718169 User have access to all departments")
    @TmsLink("3368")
    public void testUserHaveAccessToAllDepartments() {
        CorrectionAccessLevels accessLevel = CorrectionAccessLevels.ALL_DEPARTMENTS;

        step("Делаем запрос за доступом");
        Response<JsonNode> resp = rupturesClient.checkC3Access(accessLevel);

        step("Проверяем корректность ответа");
        rupturesClient.assertStockCorrectionAccess(resp, accessLevel);
    }

    @Test(description = "C23718195 User have access to one department")
    @TmsLink("3369")
    public void testUserHaveAccessToOneDepartment() {
        CorrectionAccessLevels accessLevel = CorrectionAccessLevels.ONE_DEPARTMENT;

        step("Делаем запрос за доступом");
        Response<JsonNode> resp = rupturesClient.checkC3Access(accessLevel);

        step("Проверяем корректность ответа");
        rupturesClient.assertStockCorrectionAccess(resp, accessLevel);
    }

    @Test(description = "C23718170 User do not have access in shop")
    @TmsLink("3370")
    public void testUserDoesNotHaveAccess() {
        CorrectionAccessLevels accessLevel = CorrectionAccessLevels.NO_ONE_DEPARTMENT;

        step("Делаем запрос за доступом");
        Response<JsonNode> resp = rupturesClient.checkC3Access(accessLevel);

        step("Проверяем корректность ответа");
        rupturesClient.assertStockCorrectionAccess(resp, accessLevel);
    }

    @Test(description = "C23718171 User not existed in system")
    @TmsLink("3371")
    public void testUserDoesNotExistInSystem() {
        CorrectionAccessLevels accessLevel = CorrectionAccessLevels.UNKNOWN_USER;

        step("Делаем запрос за доступом");
        Response<JsonNode> resp = rupturesClient.checkC3Access(accessLevel);

        step("Проверяем корректность ответа");
        rupturesClient.assertStockCorrectionAccess(resp, accessLevel);
    }
}
