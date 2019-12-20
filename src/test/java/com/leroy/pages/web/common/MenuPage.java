package com.leroy.pages.web.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;

public class MenuPage extends BaseWebPage {

    public MenuPage(TestContext context) {
        super(context);
    }

    @WebFindBy(id = "burgerMenuButton", metaName = "Бургер меню кнопка")
    private Button burgerMenuBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'lmui-View-column lmui-View-start')]/span",
            metaName = "Навигационные кнопки левого меню")
    private ElementList<Element> leftMenuNavButtons;


}
