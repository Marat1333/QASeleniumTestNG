package com.leroy.magmobile.ui.pages.work.print_tags;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.WorkPage;
import com.leroy.magmobile.ui.pages.work.print_tags.widgets.SessionWidget;
import io.qameta.allure.Step;

public class SessionsListPage extends CommonMagMobilePage {
    @AppFindBy(accessibilityId = "BackButton")
    Button backBtn;

    @AppFindBy(text = "Печать ценников")
    Element header;

    @AppFindBy(text = "НОВАЯ СЕССИЯ ПЕЧАТИ")
    Button createNewSessionBtn;

    @AppFindBy(xpath = "/*[@text='АКТИВНАЯ СЕССИЯ']/following-sibling:*")
    SessionWidget activeSessionWidget;

    @AppFindBy(text = "Чтобы напечатать ценники, нажми на зеленую кнопку ниже.")
    Element noCreatedSessionLbl;

    @Override
    protected void waitForPageIsLoaded() {
        header.waitForVisibility();
        createNewSessionBtn.waitForVisibility();
    }

    @Step("Перейти в активную сессию")
    public TagsListPage navigateToActiveSession(){
        activeSessionWidget.click();
        return new TagsListPage();
    }

    @Step("Создать новую сессию")
    public void createNewSession(){
        createNewSessionBtn.click();
    }

    @Step("Перейти назад")
    public WorkPage goBack(){
        backBtn.click();
        return new WorkPage();
    }

    @Step("Проверить вид отображения страницы")
    public SessionsListPage shouldViewTypeIsCorrect(boolean noSession){
        if (noSession){
            anAssert.isElementVisible(noCreatedSessionLbl);
            anAssert.isElementNotVisible(activeSessionWidget);
        }else {
            anAssert.isElementVisible(activeSessionWidget);
            anAssert.isElementNotVisible(noCreatedSessionLbl);
        }
        return this;
    }

    public void verifyRequiredElements(){
        softAssert.areElementsVisible(backBtn, header, createNewSessionBtn);
        softAssert.verifyAll();
    }
}
