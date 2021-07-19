package com.leroy.magportal.ui.tests.picking;

import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.pages.picking.mobile.PickingDocListMobilePage;
import com.leroy.magportal.ui.tests.BaseMockMagPortalUiTest;
import io.qameta.allure.AllureId;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;

public class PickingFilteringMobileTest extends BaseMockMagPortalUiTest {

    private String documentWhenClearFilters = "0000 *3598";

    @BeforeMethod
    public void setUpMock() throws Exception {
        setUpMockForTestCase();
    }

    // Небольшие общие степы

    private void stepClearFiltersAndCheck() throws Exception {
        new PickingDocListMobilePage().clickFilterButton()
                .clickClearFilters()
                .clickConfirmBtn()
                .shouldDocumentListIs(Collections.singletonList(documentWhenClearFilters));
    }

    @Test(description = "C23438168 Сборка. Фильтрация по Типу сборки")
    @AllureId("1204")
    public void testFiltersByAssemblyType() throws Exception {

        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);

        // Step 2
        step("Нажать на кебаб меню справа, выставить в фильтрах тип сборки 'Торг.зал LS', нажать 'Применить'");
        pickingPage = pickingPage.clickFilterButton()
                .selectAssemblyType(PickingConst.AssemblyType.SHOPPING_ROOM)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByAssemblyType(PickingConst.AssemblyType.SHOPPING_ROOM);

        // Step 3
        step("Повторить шаг 2 для остальных типов сборки Склад WH , CC");
        pickingPage = pickingPage.clickFilterButton()
                .selectAssemblyType(PickingConst.AssemblyType.STOCK)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByAssemblyType(PickingConst.AssemblyType.STOCK);
        pickingPage.clickFilterButton()
                .selectAssemblyType(PickingConst.AssemblyType.SS)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByAssemblyType(PickingConst.AssemblyType.SS);

        // Step 4
        step("Деактивировать все фильтры, нажать Применить");
        stepClearFiltersAndCheck();
    }

    @Test(description = "C23438169 Сборка. Фильтрация по Статусу сборки")
    @AllureId("1205")
    public void testFiltersByStatus() throws Exception {

        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);

        // Step 2
        step("Нажать на кебаб меню справа, выставить в фильтрах Статус заказа 'Готов к сборке', нажать 'Применить'");
        pickingPage = pickingPage.clickFilterButton()
                .selectPickingStatus(SalesDocumentsConst.PickingStatus.ALLOWED_FOR_PICKING)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus.ALLOWED_FOR_PICKING);

        // Step 3
        step("Повторить шаг 2 для остальных статусов Сборка, Сборка(пауза), Част.собран, Собран");
        pickingPage = pickingPage.clickFilterButton()
                .selectPickingStatus(SalesDocumentsConst.PickingStatus.PICKING_IN_PROGRESS)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus.PICKING_IN_PROGRESS);
        pickingPage = pickingPage.clickFilterButton()
                .selectPickingStatus(SalesDocumentsConst.PickingStatus.PAUSE_PICKING)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus.PAUSE_PICKING);
        pickingPage = pickingPage.clickFilterButton()
                .selectPickingStatus(SalesDocumentsConst.PickingStatus.PICKED)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus.PICKED);
        pickingPage = pickingPage.clickFilterButton()
                .selectPickingStatus(SalesDocumentsConst.PickingStatus.PARTIALLY_PICKED)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus.PARTIALLY_PICKED);

        // Step 4
        step("Активировать все статусы сборки, нажать Применить");
        pickingPage.clickFilterButton()
                .selectPickingStatus(SalesDocumentsConst.PickingStatus.ALLOWED_FOR_PICKING,
                        SalesDocumentsConst.PickingStatus.PICKING_IN_PROGRESS,
                        SalesDocumentsConst.PickingStatus.PAUSE_PICKING,
                        SalesDocumentsConst.PickingStatus.PICKED,
                        SalesDocumentsConst.PickingStatus.PARTIALLY_PICKED)
                .clickConfirmBtn()
                .shouldDocumentListIs(Collections.singletonList("8169 *8169"));

        // Step 5
        step("Нажать на кебаб меню справа, нажать 'Очистить', 'Применить'");
        stepClearFiltersAndCheck();
    }

    @Test(description = "C23438170 Сборка. Фильтрация по отделам")
    @AllureId("1206")
    public void testFiltersByDepartments() throws Exception {

        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);

        // Step 2
        step("Нажать на кебаб меню справа, выставить в фильтрах Отделы '01 Стройматериалы', нажать 'Применить'");
        pickingPage = pickingPage.clickFilterButton()
                .clickClearFilters()
                .selectDepartmentFilter("01")
                .clickConfirmBtn()
                .shouldDocumentsFilteredByDepartments("01");

        // Step 3
        step("Выставить в фильтрах Отделы от 1 до 15, нажать 'Применить'");
        String[] filtersForStep3 = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15"};
        pickingPage = pickingPage.clickFilterButton()
                .clickClearFilters()
                .selectDepartmentFilter(filtersForStep3)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByDepartments(filtersForStep3);

        // Step 4
        step("В фильтрах Отделы оставить активным только несколько отделов, выбрать Статус заказа, выбрать Тип сборки, нажать 'Применить'");
        String[] filtersForStep4 = {"01", "05"};
        pickingPage.clickFilterButton()
                .clickClearFilters()
                .selectDepartmentFilter(filtersForStep4)
                .selectPickingStatus(SalesDocumentsConst.PickingStatus.ALLOWED_FOR_PICKING)
                .selectAssemblyType(PickingConst.AssemblyType.SHOPPING_ROOM)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByDepartments(filtersForStep4)
                .shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus.ALLOWED_FOR_PICKING)
                .shouldDocumentsFilteredByAssemblyType(PickingConst.AssemblyType.SHOPPING_ROOM);

        // Step 5
        step("Нажать на кебаб меню справа, нажать 'Очистить', 'Применить'");
        stepClearFiltersAndCheck();
    }

    @Test(description = "C23438171 Сборка. Фильтрация с чек боксом 'Мои'")
    @AllureId("1207")
    public void testFilterMy() throws Exception {
        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);

        // Step 2
        step("Нажать на кебаб меню справа, активировать чек бокс 'Мои', выставить статус заказа Сборка, Сборка Пауза, Частично собран");
        pickingPage = pickingPage.clickFilterButton()
                .clickClearFilters()
                .selectFilterMy()
                .selectPickingStatus(SalesDocumentsConst.PickingStatus.PICKING_IN_PROGRESS,
                        SalesDocumentsConst.PickingStatus.PAUSE_PICKING,
                        SalesDocumentsConst.PickingStatus.PARTIALLY_PICKED)
                .clickConfirmBtn()
                .shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus.PICKING_IN_PROGRESS,
                        SalesDocumentsConst.PickingStatus.PAUSE_PICKING,
                        SalesDocumentsConst.PickingStatus.PARTIALLY_PICKED)
                .shouldDocumentsFilteredByMy()
                .shouldDocumentListIs(Collections.singletonList("8171 *9274"));

        // Step 3
        step("Добавить к фильтрам отделы 01, 02, нажать Применить");
        pickingPage.clickFilterButton()
                .selectDepartmentFilter("01", "02")
                .clickConfirmBtn()
                .shouldDocumentsFilteredByMy()
                .shouldDocumentsFilteredByStatus(SalesDocumentsConst.PickingStatus.PICKING_IN_PROGRESS,
                        SalesDocumentsConst.PickingStatus.PAUSE_PICKING,
                        SalesDocumentsConst.PickingStatus.PARTIALLY_PICKED)
                .shouldDocumentsFilteredByDepartments("01", "02")
                .shouldDocumentListIs(Collections.singletonList("8171 *0002"));
    }

    @Test(description = "C23438174 Сборка. Поиск по номеру заказа")
    @AllureId("1210")
    public void testSearchForPickingByOrderNumber() throws Exception {
        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);

        // Step 2
        step("Ввести 12-значный номер заказа");
        pickingPage.searchForPickingByOrderNumber("000023438174")
                .shouldDocumentListIs(Collections.singletonList("0001 *8174"));
    }

    @Test(description = "C23438910 Сборка. Фильтрация по Схеме продажи")
    @AllureId("1211")
    public void testFilteringBySalesScheme() throws Exception {
        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);

        // Step 2
        step("Нажать на кебаб меню справа, выставить в фильтрах тип сборки 'LT', нажать 'Применить'");
        pickingPage.clickFilterButton()
                .selectSalesScheme("LT")
                .clickConfirmBtn()
                .shouldDocumentListIs(Collections.singletonList("0001 *8174"));

        // Step 3
        step("Повторить шаг 2 для остальных cхем продажи");
        pickingPage.clickFilterButton()
                .selectSalesScheme("LT", "LTD", "SLT", "SLTD", "SLTx")
                .clickConfirmBtn()
                .shouldDocumentListIs(Collections.singletonList("0002 *8174"));

        // Step 4
        step("Повторить шаг 2 для нескольких cхем продажи одновременно");
        pickingPage.clickFilterButton()
                .clickClearFilters()
                .selectSalesScheme("LTD", "SLT", "SLTx")
                .clickConfirmBtn()
                .shouldDocumentListIs(Collections.singletonList("0003 *8174"));

        // Step 5
        step("Деактивировать все фильтры, нажать Применить");
        stepClearFiltersAndCheck();
    }

}
