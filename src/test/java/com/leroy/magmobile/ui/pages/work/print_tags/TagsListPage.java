package com.leroy.magmobile.ui.pages.work.print_tags;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.print_tags.data.ProductTagData;
import com.leroy.magmobile.ui.pages.work.print_tags.modal.ConfirmSessionExitModalPage;
import com.leroy.magmobile.ui.pages.work.print_tags.widgets.ProductWidget;
import io.qameta.allure.Step;

import java.util.List;

public class TagsListPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "Button")
    Button backBtn;

    @AppFindBy(accessibilityId = "ScreenTitle")
    Element createSessionTimeStamp;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"ScreenHeader\"]/android.view.ViewGroup[1]")
    Button deleteSessionBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"ScreenHeader\"]/android.view.ViewGroup[2]")
    Button switchToMassiveEditorModeBtn;

    AndroidScrollView<ProductTagData> productsScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, ".//android.view.ViewGroup[@content-desc=\"lmCode\"]/..",
            ProductWidget.class);

    @AppFindBy(text = "ТОВАР")
    Button addProductBtn;

    @AppFindBy(text = "НАПЕЧАТАТЬ")
    Button printSession;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"lmui-Icon\"][following-sibling::android.widget.TextView]" +
            "/following-sibling::android.widget.TextView")
    Button printerNameBtn;


    @Override
    protected void waitForPageIsLoaded() {
        createSessionTimeStamp.waitForVisibility();
        deleteSessionBtn.waitForVisibility();
    }

    @Step("Перейти на страницу выбора принтера")
    public PrinterSelectorPage goToPrinterSelectorPage(){
        printerNameBtn.click();
        return new PrinterSelectorPage();
    }

    @Step("Перейти назад")
    public ConfirmSessionExitModalPage goBack() {
        backBtn.click();
        return new ConfirmSessionExitModalPage();
    }

    @Step("Добавить товар в сессию")
    public PrintTagsScannerPage addProductToSession(){
        addProductBtn.click();
        return new PrintTagsScannerPage();
    }

    @Step("Проверить, что корректно выбран принтер")
    public TagsListPage shouldPrinterIsCorrect(String printerDepartmentName){
        anAssert.isElementTextContains(printerNameBtn, printerDepartmentName);
        return this;
    }

    @Step("Проверить, что список содержит все переданные товары")
    public TagsListPage shouldProductsAreCorrect(String...lmCodes) {
        List<ProductTagData> productTagsList = productsScrollView.getFullDataList();
        for (int i = 0; i < productTagsList.size(); i++) {
            softAssert.isEquals(productTagsList.get(i).getLmCode(), lmCodes[i], "lmCode");
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что кол-во товаров равно {count}")
    public TagsListPage shouldProductCountIsCorrect(int count){
        List<ProductTagData> productTagsList = productsScrollView.getFullDataList();
        anAssert.isEquals(productTagsList.size(), count, "products count");
        return this;
    }

    public void verifyRequiredElements(){
        softAssert.areElementsVisible(backBtn, createSessionTimeStamp, deleteSessionBtn, switchToMassiveEditorModeBtn);
        softAssert.verifyAll();
    }
}
