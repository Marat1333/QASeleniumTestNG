package com.leroy.magportal.ui.tests;

import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.models.picking.PickingTaskData;
import com.leroy.magportal.ui.models.picking.ShortPickingTaskData;
import com.leroy.magportal.ui.pages.picking.PickingContentPage;
import com.leroy.magportal.ui.pages.picking.PickingPage;
import com.leroy.magportal.ui.pages.picking.modal.SplitPickingModalStep1;
import com.leroy.magportal.ui.pages.picking.modal.SplitPickingModalStep2;
import com.leroy.magportal.ui.pages.picking.modal.SuccessfullyCreatedAssemblyModal;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

public class PickingTest extends WebBaseSteps {

    @Test(description = "C23408356 Сплит сборки (зона сборки Торговый зал)")
    public void testSplitBuildShoppingRoom() throws Exception {
        // Test data
        PickingConst.AssemblyType assemblyType = PickingConst.AssemblyType.SHOPPING_ROOM;

        PickingPage pickingPage = loginSelectShopAndGoTo(PickingPage.class);
        String assemblyNumber = "4005";
        String orderNumber = "6926";
        String fullNumber = assemblyNumber + " *" + orderNumber;
        pickingPage.clickDocumentInLeftMenu(fullNumber);
        PickingContentPage pickingContentPage = new PickingContentPage();

        // Step 1
        step("Нажать на кнопку редактирования (карандаш) в нижней части экрана");
        PickingTaskData pickingTaskData = pickingContentPage.getPickingTaskData();
        pickingContentPage.clickEditAssemblyButton();

        // Step 2
        step("Нажать на чекбокс в правом верхнем углу карточки товара");
        pickingContentPage.setSplitForProductCard(1, true)
                .shouldSelectAllOptionIsSelected();

        // Step 3
        step("Нажать на кнопку Разделить");
        SplitPickingModalStep1 splitPickingModalStep1 = pickingContentPage.clickSplitAssemblyButton()
                .verifyRequiredElements()
                .shouldContainsProducts(pickingTaskData.getProducts())
                .shouldContinueButtonIsDisabled();

        // Step 4
        step("Выбрать зону сборки Торговый зал");
        splitPickingModalStep1.selectAssemblyType(assemblyType)
                .shouldContainsProducts(pickingTaskData.getProducts()) // TODO надо еще проверить другие столбики
                .shouldContinueButtonIsDisabled();

        // Step 5
        step("Нажать чекбокс Собрать из торгового зала (LS)");
        splitPickingModalStep1.selectCollectFromShoppingRoomOption(true)
                .shouldContinueButtonIsEnabled();

        // Step 6
        step("Нажать кнопку Продолжить");
        SplitPickingModalStep2 splitPickingModalStep2 = splitPickingModalStep1.clickContinueButton()
                .verifyRequiredElements()
                .shouldIamResponsibleForAssemblyOptionSelected();

        // Step 7
        step("Заполнить комментарий и Нажать на кнопку Создать сборку");
        String comment = RandomStringUtils.randomAlphabetic(15);
        splitPickingModalStep2.enterComment(comment);
        SuccessfullyCreatedAssemblyModal successfullyCreatedAssemblyModal = splitPickingModalStep2
                .clickCreateAssemblyButton()
                .verifyRequiredElements()
                .shouldOrderNumberIs(orderNumber);
        pickingTaskData.setNumber(successfullyCreatedAssemblyModal.getAssemblyNumber());
        pickingTaskData.setAssemblyType(assemblyType);
        pickingTaskData.setCreationDate(null);

        // Step 8
        step("Нажать кнопку Перейти к новому заданию на сборку");
        pickingContentPage = successfullyCreatedAssemblyModal.clickNavigateToNewTaskButton();
        pickingContentPage.shouldPickingTaskDataIs(pickingTaskData);

        ShortPickingTaskData shortPickingTaskData = pickingContentPage.getPickingTaskData().getShortData();
        shortPickingTaskData.setCollector("Я");

        pickingContentPage.refreshDocumentList();
        pickingContentPage.shouldDocumentListContainsThis(shortPickingTaskData);
        pickingContentPage.shouldDocumentIsNotPresent(fullNumber);

        pickingContentPage.switchToCommentTab()
                .shouldCommentIs(comment);
    }

}
