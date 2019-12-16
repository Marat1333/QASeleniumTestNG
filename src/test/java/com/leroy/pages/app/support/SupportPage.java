package com.leroy.pages.app.support;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

import java.util.Arrays;
import java.util.List;

public class SupportPage extends BaseAppPage {

    public SupportPage(TestContext context) {
        super(context);
    }

    // Top Area with title and 2 buttons
    @AppFindBy(text = "Дорогая служба поддержки, хочу")
    private Element titleLbl;

    @AppFindBy(text = "пожаловаться")
    private Element complainBtnLbl;

    @AppFindBy(text = "задать вопрос")
    private Element askQuestionBtnLbl;

    // Main area

    @AppFindBy(xpath = "//android.widget.ScrollView//android.widget.TextView")
    private ElementList<Element> mainLabels;

    @Override
    public void waitForPageIsLoaded() {
        titleLbl.waitForVisibility();
    }

    /* ------------------------- ACTIONS -------------------------- */



    /* ---------------------- Verifications -------------------------- */

    @Step("Проверьте, что все элементы страницы 'Поддержка' отображаются корректно")
    public SupportPage verifyAllElementsVisibility() throws Exception {
        softAssert.isElementVisible(titleLbl);
        softAssert.isElementVisible(complainBtnLbl);
        // TODO need to check - пожаловаться (предвыбрана)
        softAssert.isElementVisible(askQuestionBtnLbl);
        List<String> expectedIssueCategories = Arrays.asList("В чем проблема?",
                "Данные о товаре", "Цена товара", "Запас на LS/RM/EM", "Не найден товар",
                "Отзыв товара со склада", "Данные клиента", "SMS-уведомления клиентам",
                "Оформление продажи", "История продаж", "Адресное хранение",
                "Управление перебоями", "Работа Wi-fi сети", "Что-то другое");
        softAssert.isEquals(mainLabels.getTextList(), expectedIssueCategories,
                "Список категорий проблем должен быть %s");
        softAssert.verifyAll();
        return this;
    }

    @Step("Нажмите на кнопку {button}")
    public ComplainPage clickButton(String button) {
        getElementByText(button).click();
        return new ComplainPage(context);
    }
}
