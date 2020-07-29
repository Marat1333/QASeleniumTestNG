package com.leroy.magmobile.ui.pages.work.print_tags.modal;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.print_tags.TagsListPage;
import com.leroy.magmobile.ui.pages.work.print_tags.enums.Format;
import io.qameta.allure.Step;

public class FormatSuccessPrintModalPage extends CommonMagMobilePage {
    @AppFindBy(text = "ОТЛИЧНО")
    Button confirmBtn;

    @AppFindBy(containsText = "отправлены на печать")
    Element header;

    @Override
    protected void waitForPageIsLoaded() {
        confirmBtn.waitForVisibility();
        header.waitForVisibility();
    }

    @Step("Подтвердить")
    public TagsListPage confirm(){
        confirmBtn.click();
        return new TagsListPage();
    }

    @Step("Проверить, что заголовок содержит нужный формат ценника")
    public FormatSuccessPrintModalPage shouldHeaderContainsFormat(Format format){
        switch (format){
            case SMALL:
                anAssert.isElementTextContains(header, "4 × 6 см");
                break;
            case MIDDLE:
                anAssert.isElementTextContains(header, "6 × 10 см");
                break;
            case BIG:
                anAssert.isElementTextContains(header, "10 × 18 см");
                break;
        }
        return this;
    }
}
