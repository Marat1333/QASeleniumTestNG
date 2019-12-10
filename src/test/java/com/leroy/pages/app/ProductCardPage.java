package com.leroy.pages.app;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

public class ProductCardPage extends BaseAppPage {

    public ProductCardPage(WebDriver driver) {
        super(driver);
    }

    @AppFindBy(accessibilityId = "RMProductCard", metaName = "Область с заголовком карточки товара")
    public Element productCardHeaderArea;

    @AppFindBy(accessibilityId = "Button")
    public Element withdrawalBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='Button']/android.widget.TextView",
            metaName = "Текст кнопки 'Отозвать'")
    public Element withdrawalBtnLabel;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup")
    private Element mainContentArea;

    // Modal window elements
    @AppFindBy(accessibilityId = "monoPalletReserved")
    private EditBox quantityItemsFld;

    @Override
    public void waitForPageIsLoaded() {
        mainContentArea.waitForVisibility();
    }

    public void enterCountOfItems(String quantity) {
        quantityItemsFld.clearAndFill(quantity);
    }

}
