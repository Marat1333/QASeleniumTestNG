package com.leroy.pages.app;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

public class ProductCardPage extends BaseAppPage {

    public ProductCardPage(WebDriver driver) {
        super(driver);
    }

    @AppFindBy(accessibilityId = "RMProductCard", metaName = "Область с заголовком карточки товара")
    public Element productCardHeaderArea;

    @AppFindBy(xpath = "//android.view.ViewGroup[android.widget.TextView[@text='ОТОЗВАТЬ']]")
    private Element withdrawBtn;

    public void clickWithDrawBtn() {

    }

}
