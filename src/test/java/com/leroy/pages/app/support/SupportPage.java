package com.leroy.pages.app.support;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import org.openqa.selenium.WebDriver;

public class SupportPage extends BaseAppPage {

    public SupportPage(WebDriver driver) {
        super(driver);
    }

    // Top Area with title and 2 buttons
    @AppFindBy(text = "Дорогая служба поддержки, хочу")
    public Element titleLbl;

    @AppFindBy(text = "пожаловаться")
    public Element complainBtnLbl;

    @AppFindBy(text = "задать вопрос")
    public Element askQuestionBtnLbl;

    // Main area

    @AppFindBy(text = "Что случилось?")
    public Element whatHappenLbl;

    @AppFindBy(text = "Товар не найден")
    public Element productNotFoundLbl;

    @AppFindBy(text = "Неверная цена")
    public Element wrongPriceLbl;

    @AppFindBy(text = "Проблема с данными клиента")
    public Element issueWithClientDataLbl;

    @AppFindBy(text = "Неверный запас в торг. зале (LS/EM)")
    public Element wrongStockInTradingRoomLbl;

    @AppFindBy(text = "Ошибка при отзыве со склада (RM)")
    public Element errorWhenRecallingFromStockLbl;

    @AppFindBy(text = "Неверный запас на складе (RM)")
    public Element wrongStockInWarehouseLbl;

    @AppFindBy(text = "Что-то другое")
    public Element somethingElseLbl;

    @Override
    public void waitForPageIsLoaded() {
        titleLbl.waitForVisibility();
    }
}
