package com.leroy.magportal.ui.tests.picking;

import com.leroy.magportal.ui.pages.picking.mobile.PickingDocListMobilePage;
import com.leroy.magportal.ui.pages.picking.mobile.PickingWaveMobilePage;
import com.leroy.magportal.ui.tests.BaseMockMagPortalUiTest;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;

import java.util.Collections;

public class PickingWavesMobileTest extends BaseMockMagPortalUiTest {

    @Test(description = "C23415580 Добавление сборок в волну")
    @TmsLink("1192")
    public void testAddPickingIntoWave() throws Exception {
        setUpMockForTestCase();

        // Step 1
        step("Открыть страницу со Сборкой");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);
        pickingPage.reloadPage();
        pickingPage = new PickingDocListMobilePage();

        // Step 2
        step("Нажать на иконку волны сборок");
        PickingWaveMobilePage pickingWaveMobilePage = pickingPage.clickPickingWageButton();

        // Step 3
        step("Проверить список товаров, если они есть");
        pickingWaveMobilePage.shouldTitleIsVisible();

        // Step 4
        step("Вернуться на экран Сборки");
        pickingWaveMobilePage.clickBackButton();

        // Step 5
        step("Выбрать несколько сборок, нажав на чекбокс 'Выбрать для сборки'");
        pickingPage = new PickingDocListMobilePage();
        pickingPage.selectAllPickingChkBoxes()
                .checkActiveButtonsVisibility(true);

        // Step 6
        step("Нажать кнопку 'Отмена'");
        pickingPage.clickCancelButton()
                .checkActiveButtonsVisibility(false);

        // Step 7
        step("Выбрать несколько сборок, нажав на чекбокс 'Выбрать для сборки', Нажать кнопку 'Начать сборку'");
        pickingPage.selectAllPickingChkBoxes()
                .clickStartPicking()
                .checkActiveButtonsVisibility(false);

        // Step 8
        step("Нажать на иконку волны сборок");
        pickingPage.clickPickingWageButton()
                .shouldTitleIsVisible();
    }


    @Test(description = "C23423641 Завершение волны сборок (все сборки собраны полностью)")
    @TmsLink("1195")
    public void testFinishPickingWave() throws Exception {
        setUpMockForTestCase();

        // Step 1
        step("Перейти в волну сборок");
        PickingDocListMobilePage pickingPage = loginAndGoTo(PickingDocListMobilePage.class);
        PickingWaveMobilePage pickingWaveMobilePage = pickingPage.clickPickingWageButton();

        // Step 2
        step("Для товаров из сборки 1 проставить собранное количество равное заказанному");
        pickingWaveMobilePage.pickingWaveItems.get(0).editCollectedField(2);

        // Step 3
        step("Для товаров из сборки 2 проставить собранное количество равное заказанному");
        pickingWaveMobilePage.pickingWaveItems.get(1).editCollectedField(10);

        // Step 4
        step("Нажать на кнопку 'Завершить все сборки'");
        pickingPage = pickingWaveMobilePage.clickFinishAllPickingButton();
        pickingPage.shouldNotAnyAlertErrorMessagesPresent();

        // Step 5
        step("В верхней части экрана на сообщении о наличии неразмещенных сборок нажать Показать");
        pickingPage.clickShowButton();
        pickingPage.shouldNotAnyAlertErrorMessagesPresent();
        pickingPage.shouldDocumentListIs(Collections.singletonList("2222 *3333"));

    }

}
