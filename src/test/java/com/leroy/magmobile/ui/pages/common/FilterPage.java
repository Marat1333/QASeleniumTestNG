package com.leroy.magmobile.ui.pages.common;

import com.leroy.constants.MagMobElementTypes;
import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.android.AndroidHorizontalScrollView;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobCheckBox;
import com.leroy.magmobile.ui.pages.common.widget.SupplierCardWidget;
import com.leroy.magmobile.ui.pages.widgets.CalendarWidget;
import com.leroy.magmobile.ui.pages.widgets.TextViewWidget;
import com.leroy.models.TextViewData;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.time.LocalDate;
import java.util.NoSuchElementException;

// TODO Надо объединить экраны "Мой магазин" и "Вся гамма ЛМ" в один класс
public class FilterPage extends BaseAppPage {

    private static final String SCREEN_TITLE = "Фильтры по товарам";

    public FilterPage(TestContext context) {
        super(context);
    }

    @Override
    public void waitForPageIsLoaded() {
        screenTitleLbl.waitUntilTextIsEqualTo(SCREEN_TITLE);
    }

    public static final String GAMMA = "ГАММА";
    public static final String TOP = "ТОП";
    public static final String TOP_1000 = "Toп 1000";
    public static final String CTM = "CTM";
    public static final String BEST_PRICE = "Лучшая цена";
    public static final String LIMITED_OFFER = "Предложение ограничено";
    public static final String MY_SHOP_FRAME_TYPE = "МОЙ МАГАЗИН";
    public static final String ALL_GAMMA_FRAME_TYPE = "ВСЯ ГАММА ЛМ";
    public static final String AVS = "AVS";
    public static final String COMMON_PRODUCT_TYPE = "ОБЫЧНЫЙ";
    public static final String ORDERED_PRODUCT_TYPE = "ПОД ЗАКАЗ";

    private final String HORIZONTAL_SCROLL = "//android.widget.TextView[contains(@text,'%s')]/ancestor::android.widget.HorizontalScrollView";

    @AppFindBy(accessibilityId = "ScreenTitle", metaName = "Загаловок экрана 'Фильтры по товарам'")
    Element screenTitleLbl;

    @AppFindBy(xpath = AndroidScrollView.TYPICAL_XPATH, metaName = "Основная прокручиваемая область страницы")
    AndroidScrollView<TextViewData> mainScrollView;

    AndroidHorizontalScrollView<TextViewData> gammaFilterScrollView = new AndroidHorizontalScrollView<>(driver,
            By.xpath("//android.widget.ScrollView//android.view.ViewGroup[3]/android.widget.HorizontalScrollView"));

    @AppFindBy(text = "МОЙ МАГАЗИН")
    MagMobCheckBox myShopBtn;

    @AppFindBy(text = "ВСЯ ГАММА ЛМ")
    Element gammaLmBtn;

    @AppFindBy(text = "ПОД ЗАКАЗ")
    Element orderedProductBtn;

    @AppFindBy(text = "ОБЫЧНЫЙ")
    Element commonProductBtn;

    @AppFindBy(text = "Дата AVS")
    Element avsDateBtn;

    @AppFindBy(text = "ПОКАЗАТЬ ТОВАРЫ")
    Element showGoodsBtn;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"ScreenHeader\"]//android.view.ViewGroup[@content-desc=\"Button\"]")
    Element clearAllFiltersBtn;

    Element bestPrice = E("contains(Лучшая цена)");

    Element top1000 = E("contains(Toп 1000)");

    Element ctm = E("contains(CTM)");

    Element limitedOffer = E("contains(Предложение ограничено)");

    Element avs = E("contains(AVS)");

    @Step("Проскроллить фильтры до {neededElement}")
    public void scrollHorizontalWidget(String neededScroll, String neededElement) {
        Element goalElement = E("contains(" + neededElement + ")");
        Element anchorElement = null;
        try {
            anchorElement = E(String.format(HORIZONTAL_SCROLL, neededScroll));
        } catch (NoSuchElementException e) {
            Log.error("Выбрана несуществующая горизонтальная область скролла. Необходимо выбрать из: ГАММА, ТОП, ОБЫЧНЫЙ (тип продукта)");
        }
        swipeRightTo(anchorElement, goalElement);

    }

    @Step("Выбрать фрейм фильтров {value}")
    public <T> T switchFiltersFrame(String value) throws Exception {
        if (!gammaLmBtn.isVisible())
            mainScrollView.scrollUp(1);
        if (value.equals(ALL_GAMMA_FRAME_TYPE)) {
            gammaLmBtn.click();
            return (T) new AllGammaFilterPage(context);
        } else {
            myShopBtn.click();
            return (T) new MyShopFilterPage(context);
        }
    }

    @Step("Очистить все фильтры")
    public MyShopFilterPage clearAllFilters() {
        clearAllFiltersBtn.click();
        clearAllFiltersBtn.waitForInvisibility();
        return new MyShopFilterPage(context);
    }

    @Step("Выбрать checkBox фильтр {value}")
    public void choseCheckBoxFilter(String value) throws Exception {
        switch (value) {
            case TOP_1000:
                top1000.click();
                break;
            case CTM:
                ctm.click();
                break;
            case BEST_PRICE:
                bestPrice.click();
                break;
            case LIMITED_OFFER:
                limitedOffer.click();
                break;
            case AVS:
                mainScrollView.scrollDown();
                avs.click();
                mainScrollView.scrollUp();
                break;
            default:
                throw new Exception();
        }
    }

    @Step("Выбрать фильтр {gamma}")
    public void choseGammaFilter(String gamma) throws Exception {
        gamma = gamma.toUpperCase();
        try {
            Element element = E("contains(" + gamma + ")");
            gammaFilterScrollView.scrollRight(element);
            element.click();
        } catch (NoSuchElementException e) {
            Log.error("Выбранная Гамма не найдена");
        }
    }

    @Step("Выбрать тип продукта {type}")
    public void choseProductType(String type) throws Exception {
        mainScrollView.scrollDown();
        if (type.equals(COMMON_PRODUCT_TYPE)) {
            commonProductBtn.click();
        } else {
            orderedProductBtn.click();
        }
    }

    @Step("Выбрать дату avs")
    public void choseAvsDate(LocalDate date) throws Exception {
        mainScrollView.scrollDown();
        avsDateBtn.click();
        CalendarWidget calendarWidget = new CalendarWidget(context.getDriver());
        calendarWidget.selectDate(date);
    }

    @Step("Показать товары по выбранным фильтрам")
    public SearchProductPage applyChosenFilters() {
        mainScrollView.scrollDownToElement(showGoodsBtn);
        showGoodsBtn.click();
        waitUntilProgressBarIsVisible();
        SearchProductPage page = new SearchProductPage(context);
        hideKeyboard();
        return page;
    }

    //Verifications

    @Step("Проверить, что выбран чек-бокс {value}")
    public FilterPage shouldElementHasBeenSelected(String value) {
        Element anchorElement = E(String.format(SupplierCardWidget.SPECIFIC_CHECKBOX_XPATH, value),
                String.format("Чек-бокс %s", value));
        anAssert.isElementImageMatches(anchorElement, MagMobElementTypes.CHECK_BOX_FILTER_PAGE.getPictureName());
        return this;
    }

    @Step("Проверить, что чек-бокс {value} не выбран")
    public FilterPage shouldElementHasNotBeenSelected(String value) {
        Element anchorElement = E(String.format(SupplierCardWidget.SPECIFIC_CHECKBOX_XPATH, value),
                String.format("Чек-бокс %s", value));
        anAssert.isElementImageMatches(anchorElement, MagMobElementTypes.CHECK_BOX_NOT_SELECTED_FILTER_PAGE.getPictureName());
        return this;
    }

    @Step("Проверить, что выбрана Радиогруппа {value}")
    public FilterPage shouldFilterHasBeenChosen(String value) throws Exception {
        MagMobCheckBox element = new MagMobCheckBox(driver, new CustomLocator(By.xpath("//*[contains(@text, '" + value + "')]")));
        anAssert.isTrue(element.isChecked(), "Фильтр '" + value + "' должен быть выбран");
        return this;
    }

    @Step("Проверить, что Радиогруппа {value} НЕ выбрана")
    public FilterPage shouldFilterHasNotBeenChosen(String value) throws Exception {
        MagMobCheckBox element = new MagMobCheckBox(driver, new CustomLocator(By.xpath("//*[contains(@text, '" + value + "')]")));
        anAssert.isFalse(element.isChecked(), "Фильтр '" + value + "' не должен быть выбран");
        return this;
    }

    @Step("Проверить, что кнопка \"Метла\" должна быть видна - {isVisible}")
    public FilterPage shouldClearAllFiltersBeOnPage(boolean isVisible){
        String pageSource = getPageSource();
        if (isVisible) {
            anAssert.isElementVisible(clearAllFiltersBtn, pageSource);
        }else {
            anAssert.isElementNotVisible(clearAllFiltersBtn, pageSource);
        }
        return new FilterPage(context);
    }

}
