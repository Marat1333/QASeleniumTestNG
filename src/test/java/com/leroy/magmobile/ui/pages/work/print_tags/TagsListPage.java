package com.leroy.magmobile.ui.pages.work.print_tags;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.print_tags.data.ProductTagData;
import com.leroy.magmobile.ui.pages.work.print_tags.modal.ConfirmSessionExitModalPage;
import com.leroy.magmobile.ui.pages.work.print_tags.modal.EditTagModalPage;
import com.leroy.magmobile.ui.pages.work.print_tags.widgets.ProductWidget;
import com.leroy.utils.DateTimeUtil;
import io.qameta.allure.Step;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class TagsListPage extends CommonMagMobilePage {
    private final static int SWIPE_DEAD_ZONE_PERCENTAGE = 30;

    @AppFindBy(accessibilityId = "Button")
    Button backBtn;

    @AppFindBy(accessibilityId = "ScreenTitle")
    Element header;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"ScreenHeader\"]/android.view.ViewGroup[1]")
    Button deleteSessionBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"ScreenHeader\"]/android.view.ViewGroup[2]")
    Button switchToGroupEditorModeBtn;

    AndroidScrollView<ProductTagData> productsScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, ".//android.view.ViewGroup[@content-desc=\"lmCode\"]/..",
            ProductWidget.class);

    @AppFindBy(xpath = "//android.widget.ScrollView//android.widget.ScrollView", metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<String> mainScrollView;

    @AppFindBy(text = "ТОВАР")
    Button addProductBtn;

    @AppFindBy(text = "НАПЕЧАТАТЬ")
    Button printSession;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"lmui-Icon\"][following-sibling::android.widget.TextView]" +
            "/following-sibling::android.widget.TextView")
    Button printerNameBtn;

    @AppFindBy(text = "ИЗМЕНИТЬ ФОРМАТЫ")
    Button changeFormatBtn;

    @AppFindBy(text = "ВЫБРАТЬ ВСЕ")
    Button choseAllProductsBtn;

    @Override
    protected void waitForPageIsLoaded() {
        //TODO обработать ошибку при неполучении принтеров
        header.waitForVisibility();
        deleteSessionBtn.waitForVisibility();
        waitUntilProgressBarIsInvisible(short_timeout);
    }

    public LocalDateTime getSessionCreationTimeStamp(){
        return DateTimeUtil.strToLocalDateTime(header.getText(),DateTimeUtil.DD_MMMM_HH_MM);
    }

    public String getCurrentPrinterName(){
        return printerNameBtn.getText();
    }

    @Step("Напечатать ценники")
    public void printTags() {
        printSession.click();
        printSession.waitForInvisibility();
    }

    @Step("Перейти на страницу выбора принтера")
    public PrinterSelectorPage goToPrinterSelectorPage() {
        printerNameBtn.click();
        return new PrinterSelectorPage();
    }

    @Step("Перейти назад")
    public ConfirmSessionExitModalPage goBack() {
        backBtn.click();
        return new ConfirmSessionExitModalPage();
    }

    @Step("Добавить товар в сессию")
    public PrintTagsScannerPage addProductToSession() {
        addProductBtn.click();
        return new PrintTagsScannerPage();
    }

    @Step("Открыть модальное окно редактирования формата и кол-ва у первого товара")
    public EditTagModalPage callEditModalToProductByIndex(int index) throws Exception{
        productsScrollView.clickElemByIndex(index);
        return new EditTagModalPage();
    }

    @Step("Вызвать модалку редактирования для товара с кодом {lmCode}")
    public EditTagModalPage callEditModal(String lmCode) {
        Element productCard = E("ЛМ " + lmCode);
        if (!productCard.isVisible()) {
            mainScrollView.scrollDownToElement(productCard);
        }
        productCard.click();
        return new EditTagModalPage();
    }

    @Step("Переключится на режим массового редактирования")
    public TagsListPage switchToGroupEditorMode() {
        switchToGroupEditorModeBtn.click();
        changeFormatBtn.waitForVisibility();
        choseAllProductsBtn.waitForVisibility();
        return this;
    }

    @Step("Выбрать товар")
    public void clickOnProduct(String...lmCodes){
        String chosenProductCount;
        Element product;
        for (String eachLm : lmCodes) {
            chosenProductCount = header.getText();
            product = E("ЛМ " + eachLm);
            if (!product.isVisible()) {
                mainScrollView.scrollDownToElement(product);
            }
            product.click();
            header.waitUntilTextIsChanged(chosenProductCount);
            mainScrollView.scrollToBeginning();
        }
        changeFormatBtn.click();
    }

    @Step("Выбрать товары для редактирования и открыть модалку редактирования")
    public EditTagModalPage choseProductsAndOpenGroupEditModal(String... lmCodes) {
        String chosenProductCount;
        Element product;
        for (String eachLm : lmCodes) {
            chosenProductCount = header.getText();
            product = E("ЛМ " + eachLm);
            if (!product.isVisible()) {
                mainScrollView.scrollDownToElement(product);
            }
            product.click();
            header.waitUntilTextIsChanged(chosenProductCount);
            mainScrollView.scrollToBeginning();
        }
        changeFormatBtn.click();
        return new EditTagModalPage();
    }

    @Step("Выбрать все товары в групповом редактировании и открыть модалку редактирования")
    public EditTagModalPage choseAllProductsAndCallEditModal(){
        choseAllProductsBtn.click();
        changeFormatBtn.click();
        return new EditTagModalPage();
    }

    @Step("Проверить, что корректно выбран принтер")
    public TagsListPage shouldPrinterIsCorrect(String printerDepartmentName) {
        anAssert.isElementTextContains(printerNameBtn, printerDepartmentName);
        return this;
    }

    @Step("Проверить, что список содержит все переданные товары")
    public TagsListPage shouldProductsAreCorrect(String... lmCodes) {
        productsScrollView.setSwipeDeadZonePercentage(SWIPE_DEAD_ZONE_PERCENTAGE);
        List<ProductTagData> productTagsList = productsScrollView.getFullDataList();
        for (int i = 0; i < productTagsList.size(); i++) {
            softAssert.isEquals(productTagsList.get(i).getLmCode(), lmCodes[i], "lmCode");
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что список для всех товаров указано правильное кол-во ценников")
    public TagsListPage shouldProductTagsHasCorrectSizesAndQuantity(ProductTagData...userTagData) {
        productsScrollView.setSwipeDeadZonePercentage(SWIPE_DEAD_ZONE_PERCENTAGE);
        List<ProductTagData> uiProductTagsList = productsScrollView.getFullDataList();
        for (int i = 0; i < userTagData.length; i++) {
            softAssert.isEquals(uiProductTagsList.get(i), userTagData[i], "sizes or quantity mismatch");
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что кол-во товаров равно {count}")
    public TagsListPage shouldProductCountIsCorrect(int count) {
        productsScrollView.setSwipeDeadZonePercentage(SWIPE_DEAD_ZONE_PERCENTAGE);
        List<ProductTagData> productTagsList = productsScrollView.getFullDataList();
        anAssert.isEquals(productTagsList.size(), count, "products count");
        return this;
    }

    @Step("Проверить, что список не содержит товара")
    public TagsListPage shouldProductDeleted(String lmCode) {
        productsScrollView.setSwipeDeadZonePercentage(SWIPE_DEAD_ZONE_PERCENTAGE);
        List<ProductTagData> productTagsList = productsScrollView.getFullDataList();
        List<String> uiLmCodes = productTagsList.stream().map(ProductTagData::getLmCode).collect(Collectors.toList());
        anAssert.isFalse(uiLmCodes.contains(lmCode), "в списке содержится товар " + lmCode);
        return this;
    }

    public void verifyRequiredElements() {
        softAssert.areElementsVisible(backBtn, header, deleteSessionBtn, switchToGroupEditorModeBtn);
        softAssert.verifyAll();
    }
}
