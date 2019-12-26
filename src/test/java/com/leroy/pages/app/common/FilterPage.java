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

    public final String TEXT_AND_CHECKBOXES_GROUP_XPATH = "//android.view.ViewGroup[@content-desc='Button']/ancestor::android.view.ViewGroup[2]";
    public final String TOP_EM = "Топ ЕМ";
    public final String TOP_1000 = "Toп 1000";
    public final String CTM = "CTM";
    public final String HAS_AVAILABLE_STOCK = "Есть теор. запас";
    public final String BEST_PRICE = "Лучшая цена";
    public final String LIMITED_OFFER = "Предложение ограничено";
    public final String AVS = "AVS";

    public final String COMMON_PRODUCT_TYPE = "ОБЫЧНЫЙ";
    public final String ORDERED_PRODUCT_TYPE = "ПОД ЗАКАЗ";

    private final String CHECKBOXES_BY_TEXT_FP_XPATH = "/android.widget.TextView[@text='";
    private final String CHECKBOXES_BY_TEXT_SP_XPATH ="']/following-sibling::android.view.ViewGroup";

    @Step("Выбрать checkBox фильтр {value}")
    public void choseCheckBoxFilter(String value) throws Exception{
        switch (value){
            case TOP_EM:
                driver.findElement(By.xpath(TEXT_AND_CHECKBOXES_GROUP_XPATH+CHECKBOXES_BY_TEXT_FP_XPATH+TOP_EM+CHECKBOXES_BY_TEXT_SP_XPATH)).click();
                break;
            case TOP_1000:
                driver.findElement(By.xpath(TEXT_AND_CHECKBOXES_GROUP_XPATH+CHECKBOXES_BY_TEXT_FP_XPATH+TOP_1000+CHECKBOXES_BY_TEXT_SP_XPATH)).click();
                break;
            case CTM:
                driver.findElement(By.xpath(TEXT_AND_CHECKBOXES_GROUP_XPATH+CHECKBOXES_BY_TEXT_FP_XPATH+CTM+CHECKBOXES_BY_TEXT_SP_XPATH)).click();
                break;
            case HAS_AVAILABLE_STOCK:
                driver.findElement(By.xpath(TEXT_AND_CHECKBOXES_GROUP_XPATH+CHECKBOXES_BY_TEXT_FP_XPATH+HAS_AVAILABLE_STOCK+CHECKBOXES_BY_TEXT_SP_XPATH)).click();
                break;
            case BEST_PRICE:
                driver.findElement(By.xpath(TEXT_AND_CHECKBOXES_GROUP_XPATH+CHECKBOXES_BY_TEXT_FP_XPATH+BEST_PRICE+CHECKBOXES_BY_TEXT_SP_XPATH)).click();
                break;
            case LIMITED_OFFER:
                driver.findElement(By.xpath(TEXT_AND_CHECKBOXES_GROUP_XPATH+CHECKBOXES_BY_TEXT_FP_XPATH+LIMITED_OFFER+CHECKBOXES_BY_TEXT_SP_XPATH)).click();
                break;
            case AVS:
                new Element(driver, By.xpath(TEXT_AND_CHECKBOXES_GROUP_XPATH+CHECKBOXES_BY_TEXT_FP_XPATH+AVS+CHECKBOXES_BY_TEXT_SP_XPATH)).click();
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
        scrollUp();
        top0Btn.click();
    }

    @Step("Выбрать фильтр гамма")
    public void choseGammaFilter(){
        scrollUp();
        gammaABtn.click();
    }

    @Step("Показать товары по выбранным фильтрам")
    public SearchProductPage applyChosenFilters(){
        showGoodsBtn.click();
        return new SearchProductPage(context);
    }

}
