package com.leroy.magportal.ui.pages.customers;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magportal.ui.pages.common.MenuPage;

public class CreatingCustomerPage extends MenuPage {

    public CreatingCustomerPage(Context context) {
        super(context);
    }

    @WebFindBy(xpath = "//span[text()='Клиенты']", metaName = "Основной заголовок страницы")
    Element headerLbl;

    private CreateCustomerForm createCustomerForm = new CreateCustomerForm(context);

    public CreateCustomerForm getCreateCustomerForm() {
        return createCustomerForm;
    }

}
