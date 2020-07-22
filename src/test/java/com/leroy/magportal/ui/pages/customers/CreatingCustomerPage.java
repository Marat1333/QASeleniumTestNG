package com.leroy.magportal.ui.pages.customers;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;

public class CreatingCustomerPage extends MagPortalBasePage {

    @WebFindBy(xpath = "//span[text()='Клиенты']", metaName = "Основной заголовок страницы")
    Element headerLbl;

    private CreateCustomerForm createCustomerForm = new CreateCustomerForm();

    public CreateCustomerForm getCreateCustomerForm() {
        return createCustomerForm;
    }

}
