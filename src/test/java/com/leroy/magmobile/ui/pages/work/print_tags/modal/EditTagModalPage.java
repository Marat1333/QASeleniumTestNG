package com.leroy.magmobile.ui.pages.work.print_tags.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.print_tags.TagsListPage;
import io.qameta.allure.Step;

public class EditTagModalPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "CloseModal")
    Button closeModalBtn;

    @AppFindBy(text = "Форматы и кол-во ценников")
    Element header;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc=\"lmCode\"]")
    Element lmCode;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc=\"barCode\"]")
    Element barCode;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"barCode\"]/following-sibling::*[1]")
    Element title;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"barCode\"]/following-sibling::*[2]")
    Element price;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"barCode\"]/following-sibling::android.widget.TextView[3]")
    Element dateOfPriceChangeLbl;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Button-container\"][last()]")
    Button addProductBtn;

    @Override
    protected void waitForPageIsLoaded() {
        header.waitForVisibility();
        lmCode.waitForVisibility();
        addProductBtn.waitForVisibility();
    }

    @Step("Добавить товар в сессию печати ценников")
    public TagsListPage addProductToPrintSession(){
        addProductBtn.click();
        return new TagsListPage();
    }
}
