package com.leroy.pages.web;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.pages.web.common.MenuPage;

public class ClientPersonalInfoPage extends MenuPage {

    public ClientPersonalInfoPage(TestContext context) {
        super(context);
    }

    @WebFindBy(xpath = "//h5[contains(@class, 'Header')]",
            metaName = "Заголовок страницы с именем клиента")
    Element headerClientNameLbl;

    @WebFindBy(xpath = "//div[@class='lmui-View lmui-View-pt-gap4']/p[2]")
    Element genderObj;

    //@WebFindBy(xpath = "//div[@class='lmui-View lmui-View-pt-gap4']/p[6]")
    //Element genderObj;

}
