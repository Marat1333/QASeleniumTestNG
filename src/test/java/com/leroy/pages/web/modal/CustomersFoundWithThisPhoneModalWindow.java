package com.leroy.pages.web.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CustomersFoundWithThisPhoneModalWindow extends Element { // TODO Widget

    public CustomersFoundWithThisPhoneModalWindow(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public CustomersFoundWithThisPhoneModalWindow(WebDriver driver, CustomLocator locator, String name) {
        super(driver, locator, name);
    }

    public CustomersFoundWithThisPhoneModalWindow(WebDriver driver, WebElement we, CustomLocator locator) {
        super(driver, we, locator);
    }

    private static final String MAIN_DIV_XPATH = "//div[contains(@class, 'modal-content')]";

    @WebFindBy(text = "Найдены клиенты с этим телефоном")
    public Element customersFoundWithThisPhoneLbl;

    @WebFindBy(text = "Ты можешь перейти к существующему клиенту или вернуться к созданию нового.")
    public Element subHeaderMsgLbl;

    @WebFindBy(xpath = "//span[text()='Вернуться']/ancestor::button", metaName = "Кнопка Вернуться")
    public Element returnBtn;

}
