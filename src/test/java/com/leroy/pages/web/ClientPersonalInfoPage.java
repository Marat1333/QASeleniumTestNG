package com.leroy.pages.web;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.models.CustomerData;
import com.leroy.pages.web.common.MenuPage;

public class ClientPersonalInfoPage extends MenuPage {

    public ClientPersonalInfoPage(TestContext context) {
        super(context);
    }

    @WebFindBy(xpath = "//h5[contains(@class, 'Header')]",
            metaName = "Заголовок страницы с именем клиента")
    Element headerClientNameLbl;

    @WebFindBy(xpath = "//div/p[contains(.,'Пол')]/following-sibling::p[1]")
    Element genderObj;

    @WebFindBy(xpath = "//div/p[contains(.,'Телефон')]/following-sibling::p[1]")
    Element phoneObj;

    @WebFindBy(xpath = "//div/p[contains(.,'Email')]/following-sibling::p[1]")
    Element emailObj;

    @WebFindBy(xpath = "//div/p[contains(.,'Офис')]/following-sibling::p[1]")
    Element officeObj;

    // Verifications
    public ClientPersonalInfoPage shouldCustomerDataOnPageIs(CustomerData data) {

        softAssert.verifyAll();
        return this;
    }

}
