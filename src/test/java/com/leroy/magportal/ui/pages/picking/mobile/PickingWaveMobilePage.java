package com.leroy.magportal.ui.pages.picking.mobile;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

public class PickingWaveMobilePage extends MagPortalBasePage {

    @WebFindBy(xpath = "//div[contains(@class, 'PickingWaveHeader')]//button", metaName = "Кнопка назад")
    Element backBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'PickingWaveHeader')]//span[contains(text(), 'Все')]",
            metaName = "Загаловок страницы")
    Element title;

    @WebFindBy(xpath = "//div[contains(@class, 'Picking-PickingWaveList__list')]/div[contains(@class, 'PickingWaveItem')]",
            clazz = ProductWaveWidget.class)
    public CardWebWidgetList<ProductWaveWidget, Object> pickingWaveItems;

    @WebFindBy(xpath = "//div[contains(@class, 'PickingWaveList__action-button')]//button")
    Element finishAllPickingBtn;

    @Override
    protected void waitForPageIsLoaded() {
        title.waitForVisibility();
    }

    // Actions

    @Step("Нажать кнопку назад")
    public void clickBackButton() {
        backBtn.clickJS();
    }

    @Step("Нажать кнопку 'Завершить все сборки'")
    public PickingDocListMobilePage clickFinishAllPickingButton() {
        finishAllPickingBtn.click();
        return new PickingDocListMobilePage();
    }

    // Verifications

    @Step("Проверить, что загаловок страницы отображается")
    public PickingWaveMobilePage shouldTitleIsVisible() {
        anAssert.isElementVisible(title);
        return this;
    }


    // Widgets

    public static class ProductWaveWidget extends CardWebWidget<Object> {

        @WebFindBy(xpath = ".//div[contains(@class, 'ProductCard__inputCounter') and contains(., 'Собрано')]//input")
        EditBox collectedInput;

        public ProductWaveWidget(WebDriver driver, CustomLocator locator) {
            super(driver, locator);
        }

        public void editCollectedField(Object value) {
            collectedInput.scrollTo();
            collectedInput.clearFillAndSubmit(value.toString());
            collectedInput.sendBlurEvent();
        }

        @Override
        public Object collectDataFromPage() throws Exception {
            return null;
        }
    }


}
