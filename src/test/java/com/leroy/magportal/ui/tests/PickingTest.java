package com.leroy.magportal.ui.tests;

import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.pages.picking.PickingContentPage;
import com.leroy.magportal.ui.pages.picking.PickingPage;
import org.testng.annotations.Test;

public class PickingTest extends WebBaseSteps {

    @Test(description = "C23408356 Сплит сборки (зона сборки Торговый зал)")
    public void testSplitBuildShoppingRoom() throws Exception {
        PickingPage pickingPage = loginSelectShopAndGoTo(PickingPage.class);

        pickingPage.clickDocumentInLeftMenu("4912 *0571");
        PickingContentPage pickingContentPage = new PickingContentPage();

        // Step 1
        step("Нажать на кнопку редактирования (карандаш) в нижней части экрана");
        pickingContentPage.clickEditAssemblyButton();

        // Step 2
        step("Нажать на чекбокс в правом верхнем углу карточки товара");
        pickingContentPage.setSplitForProductCard(1, true);

        // Step 3
        step("Нажать на кнопку Разделить");

        // Step 4
        step("Выбрать зону сборки Торговый зал");

        // Step 5
        step("Нажать чекбокс Собрать из торгового зала (LS)");

        // Step 6
        step("Нажать кнопку Продолжить");

        // Step 7
        step("Заполнить комментарий и Нажать на кнопку Создать сборку");

        // Step 8
        step("Нажать кнопку Перейти к новому заданию на сборку");


        String s = "";
    }

}
