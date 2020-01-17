package com.leroy.magportal.ui.pages.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import org.openqa.selenium.WebDriver;

public class CustomersFoundWithThisPhoneModalWindow extends BaseWidget {

    public CustomersFoundWithThisPhoneModalWindow(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    private static final String MAIN_DIV_XPATH = "//div[contains(@class, 'modal-content')]";

    @WebFindBy(text = "Найдены клиенты с этим телефоном")
    public Element customersFoundWithThisPhoneLbl;

    @WebFindBy(text = "Ты можешь перейти к существующему клиенту или вернуться к созданию нового.")
    public Element subHeaderMsgLbl;

    @WebFindBy(xpath = "//span[text()='Вернуться']/ancestor::button", metaName = "Кнопка Вернуться")
    public Element returnBtn;

    @WebFindBy(xpath = MAIN_DIV_XPATH + "//div[contains(@class, 'CustomerList__items')]/div")
    public ElementList<Element> customerRows;

}
