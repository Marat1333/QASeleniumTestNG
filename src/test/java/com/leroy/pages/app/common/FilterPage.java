package com.leroy.pages.app.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class FilterPage extends BaseAppPage {

    public FilterPage(TestContext context){
        super(context);
    }

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

    public final String TEXT_AND_CHECKBOXES_GROUP_XPATH = "//android.view.ViewGroup[@content-desc=Button]/ancestor::android.view.ViewGroup[2]";
    public final String TOP_EM = "Топ ЕМ";
    public final String TOP_1000 = "Toп 1000";
    public final String CTM = "CTM";
    public final String HAS_AVAILABLE_STOCK = "Есть теор. запас";
    public final String BEST_PRICE = "Лучшая цена";
    public final String LIMITED_OFFER = "Предложение ограничено";
    public final String AVS = "AVS";

    private final String CHECKBOXES_BY_TEXT_FP_XPATH = "./android.widget.TextView[@text='";
    private final String CHECKBOXES_BY_TEXT_SP_XPATH ="']/following-sibling::android.view.ViewGroup";

    @Step("Выбрать checkBox фильтр {value}")
    public void choseCheckBoxFilter(String value) throws Exception{
        switch (value){
            case TOP_EM:
                scrollUp();
                driver.findElement(By.xpath(TEXT_AND_CHECKBOXES_GROUP_XPATH+CHECKBOXES_BY_TEXT_FP_XPATH+TOP_EM+CHECKBOXES_BY_TEXT_SP_XPATH)).click();
            case TOP_1000:
                scrollUp();
                driver.findElement(By.xpath(TEXT_AND_CHECKBOXES_GROUP_XPATH+CHECKBOXES_BY_TEXT_FP_XPATH+TOP_1000+CHECKBOXES_BY_TEXT_SP_XPATH)).click();
            case CTM:
                scrollUp();
                driver.findElement(By.xpath(TEXT_AND_CHECKBOXES_GROUP_XPATH+CHECKBOXES_BY_TEXT_FP_XPATH+CTM+CHECKBOXES_BY_TEXT_SP_XPATH)).click();
            case HAS_AVAILABLE_STOCK:
                scrollUp();
                driver.findElement(By.xpath(TEXT_AND_CHECKBOXES_GROUP_XPATH+CHECKBOXES_BY_TEXT_FP_XPATH+HAS_AVAILABLE_STOCK+CHECKBOXES_BY_TEXT_SP_XPATH)).click();
            case BEST_PRICE:
                scrollUp();
                driver.findElement(By.xpath(TEXT_AND_CHECKBOXES_GROUP_XPATH+CHECKBOXES_BY_TEXT_FP_XPATH+BEST_PRICE+CHECKBOXES_BY_TEXT_SP_XPATH)).click();
            case LIMITED_OFFER:
                scrollUp();
                driver.findElement(By.xpath(TEXT_AND_CHECKBOXES_GROUP_XPATH+CHECKBOXES_BY_TEXT_FP_XPATH+LIMITED_OFFER+CHECKBOXES_BY_TEXT_SP_XPATH)).click();
            case AVS:
                scrollDown();
                driver.findElement(By.xpath(TEXT_AND_CHECKBOXES_GROUP_XPATH+CHECKBOXES_BY_TEXT_FP_XPATH+AVS+CHECKBOXES_BY_TEXT_SP_XPATH)).click();
            default:
                throw new Exception();
        }
    }


}
