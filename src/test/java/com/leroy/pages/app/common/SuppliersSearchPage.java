package com.leroy.pages.app.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class SuppliersSearchPage extends BaseAppPage {

    public SuppliersSearchPage (TestContext context){
        super(context);
    }

    @AppFindBy(accessibilityId = "ScreenTitle-SuppliesSearch")
    EditBox searchString;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Button-container\"]/android.view.ViewGroup")
    Element confirmBtn;

    private final String EACH_SUPPLIER_NOTE_XPATH = "//android.widget.TextView[2]/ancestor::android.view.ViewGroup[1]";
    private final String SUPPLIER_NAME_XPATH="/android.widget.TextView[@text='";
    private final String SUPPLIER_CODE_XPATH="/android.widget.TextView[@text='Код: ";
    private final String SUPPLIER_SP_XPATH = "']";

    @Step("Найти поставщика по {value} и выбрать его")
    public void searchSupplier(String value){
        searchString.clearFillAndSubmit(value);

        if (value.matches("\\d+")) {
            new Element(driver, (By.xpath(EACH_SUPPLIER_NOTE_XPATH +SUPPLIER_CODE_XPATH+value+SUPPLIER_SP_XPATH))).click();
        }else {
            new Element(driver, (By.xpath(EACH_SUPPLIER_NOTE_XPATH +SUPPLIER_NAME_XPATH+value+SUPPLIER_SP_XPATH))).click();
        }
    }

    @Step("Подтвердить выбор")
    public FilterPage confirmChosenSupplier(){
        confirmBtn.click();
        return new FilterPage(context);
    }
}
