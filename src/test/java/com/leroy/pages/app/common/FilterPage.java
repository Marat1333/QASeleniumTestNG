package com.leroy.pages.app.common;

import com.leroy.constants.MagMobElementTypes;
import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.pages.app.common.widget.SupplierCardWidget;
import com.leroy.pages.app.widgets.CalendarWidget;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.time.LocalDate;
import java.util.Date;

public class FilterPage extends BaseAppPage {

    public FilterPage(TestContext context){
        super(context);
    }

    public final String GAMMA = "ГАММА";
    public final String TOP = "ТОП";
    public final String TOP_EM = "Топ ЕМ";
    public final String TOP_1000 = "Toп 1000";
    public final String CTM = "CTM";
    public final String HAS_AVAILABLE_STOCK = "Есть теор. запас";
    public final String BEST_PRICE = "Лучшая цена";
    public final String LIMITED_OFFER = "Предложение ограничено";
    public final String AVS = "AVS";
    public final String COMMON_PRODUCT_TYPE = "ОБЫЧНЫЙ";
    public final String ORDERED_PRODUCT_TYPE = "ПОД ЗАКАЗ";

    @AppFindBy(text = "МОЙ МАГАЗИН")
    Element myShopBtn;

    @AppFindBy(text = "ВСЯ ГАММА ЛМ")
    Element gammaLmBtn;

    @AppFindBy(text = "ГАММА A")
    Element gammaABtn;

    @AppFindBy(text = "ТОП 0")
    Element top0Btn;

    @AppFindBy(text="ПОД ЗАКАЗ")
    Element orderedProductBtn;

    @AppFindBy(text = "ОБЫЧНЫЙ")
    Element commonProductBtn;

    @AppFindBy(text = "Поставщик")
    Element supplierBtn;

    @AppFindBy(text = "Дата AVS")
    Element avsDateBtn;

    @AppFindBy(text = "ПОКАЗАТЬ ТОВАРЫ")
    Element showGoodsBtn;

    Element bestPrice = E("contains(Лучшая цена)");

    Element topEm = E("contains(Топ ЕМ)");

    Element top1000 = E("contains(Toп 1000)");

    Element ctm = E("contains(CTM)");

    Element hasAvailableStock = E("contains(Есть теор. запас)");

    Element limitedOffer = E("contains(Предложение ограничено)");

    Element avs = E("contains(AVS)");

    @Step("Выбрать checkBox фильтр {value}")
    public void choseCheckBoxFilter(String value) throws Exception{
        switch (value){
            case TOP_EM:
                topEm.click();
                break;
            case TOP_1000:
                top1000.click();
                break;
            case CTM:
                ctm.click();
                break;
            case HAS_AVAILABLE_STOCK:
                hasAvailableStock.click();
                break;
            case BEST_PRICE:
                bestPrice.click();
                break;
            case LIMITED_OFFER:
                limitedOffer.click();
                break;
            case AVS:
                scrollDown();
                avs.click();
                scrollUp();
                break;
            default:
                throw new Exception();
        }
    }

    @Step("Выбрать тип продукта {type}")
    public void choseProductType(String type){
        scrollDown();
        if (type.equals(COMMON_PRODUCT_TYPE)){
            commonProductBtn.click();
        }else {
            orderedProductBtn.click();
        }
    }

    @Step("Перейти на страницу выбора поставщиков")
    public SuppliersSearchPage goToSuppliersSearchPage(){
        scrollDown();
        supplierBtn.click();
        return new SuppliersSearchPage(context);
    }

    @Step("Выбрать фильтр top")
    public void choseTopFilter(){
        top0Btn.click();
    }

    @Step("Выбрать фильтр гамма")
    public void choseGammaFilter(){
        gammaABtn.click();
    }

    @Step("Выбрать дату avs")
    public void choseAvsDate(LocalDate date)throws Exception{
        avsDateBtn.click();
        CalendarWidget calendarWidget = new CalendarWidget(context.getDriver());
        calendarWidget.selectDate(date);
    }

    @Step("Показать товары по выбранным фильтрам")
    public SearchProductPage applyChosenFilters(){
        showGoodsBtn.waitForVisibility();
        scrollDown();
        showGoodsBtn.click();
        return new SearchProductPage(context);
    }

    //Verifications

    public FilterPage verifyElementIsSelected(String value){
        Element anchorElement = E(String.format(SupplierCardWidget.SPECIFIC_CHECKBOX_XPATH,value));
        anAssert.isElementImageMatches(anchorElement, MagMobElementTypes.CHECK_BOX_FILTER_PAGE.getPictureName());
        return this;
    }

    public FilterPage verifyFilterHasBeenChosen(String value)throws Exception{
        Element element = E("contains("+value+")");
        anAssert.isElementChosen(element);
        return this;
    }

}
