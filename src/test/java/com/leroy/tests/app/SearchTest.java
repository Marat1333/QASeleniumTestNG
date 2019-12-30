package com.leroy.tests.app;

import com.leroy.constants.EnvConstants;
import com.leroy.elements.MagMobCheckBox;
import com.leroy.models.UserData;
import com.leroy.pages.LoginPage;
import com.leroy.pages.app.common.*;
import com.leroy.pages.app.sales.ProductCardPage;
import com.leroy.pages.app.sales.SalesPage;
import com.leroy.tests.BaseState;
import org.testng.annotations.Test;
import org.openqa.selenium.support.Color;
import java.time.LocalDate;

public class SearchTest extends BaseState {

    private static final String ALL_DEPARTMENTS_TEXT = "Все отделы";

    @Test(description = "C3200996 Поиск товара по критериям")
    public void testC3200996() throws Exception {

        String lmCode = "10008698";
        String searchContext = "Тепломир радиатор";
        String barCode = "5902120110575";
        String shortLmCode = "100";
        String shortBarCode = "590212011";
        UserData seller = new UserData(EnvConstants.BASIC_USER_NAME, EnvConstants.BASIC_USER_PASS);

        // Pre-conditions
        LoginPage loginPage = new LoginPage(context);
        loginPage.loginInAndGoTo(seller, LoginPage.SALES_SECTION);

        // Step 1
        log.step("Нажмите на поле Поиск товаров и услуг");
        SalesPage salesPage = new SalesPage(context);
        SearchProductPage searchProductPage = salesPage.clickSearchBar()
                .verifyRequiredElements();
        // Step 2
        log.step("Перейдите в окно выбора единицы номенклатуры");
        NomenclatureSearchPage nomenclatureSearchPage = searchProductPage.goToNomenclatureWindow()
                .verifyRequiredElements();
        // Step 3
        log.step("Вернитесь на окно выбора отдела");
        nomenclatureSearchPage.returnToDepartmentChoseWindow()
                .shouldTitleWithNomenclatureIs("")
                .verifyNomenclatureBackBtnVisibility(false);
        // Step 4
        log.step("Нажмите 'Показать все товары'");
        nomenclatureSearchPage.clickShowAllProductsBtn()
                .verifyRequiredElements()
                .shouldCountOfProductsOnPageMoreThan(1)
                .shouldSelectedNomenclatureIs(ALL_DEPARTMENTS_TEXT);

        // Step 5
        log.step("Введите полное значение для поиска по ЛМ коду| 10008698");
        searchProductPage.enterTextInSearchField(lmCode);
        ProductCardPage productCardPage = new ProductCardPage(context)
                .verifyRequiredElements()
                .shouldProductLMCodeIs(lmCode);
        productCardPage.returnBack();

        // Step 6
        log.step("Введите название товара для поиска");
        searchProductPage.enterTextInSearchField(searchContext);
        searchProductPage.shouldProductCardsContainText(searchContext);

        // Step 7
        log.step("Ввести штрихкод вручную");
        searchProductPage.enterTextInSearchField(barCode);
        productCardPage = new ProductCardPage(context)
                .shouldProductBarCodeIs(barCode);
        searchProductPage = productCardPage.returnBack();

        // Step 8
        log.step("Введите часть ЛМ кода для поиска");
        searchProductPage.enterTextInSearchField(shortLmCode);
        searchProductPage.shouldProductCardsContainText(shortLmCode);

        // Step 9
        log.step("Ввести в поисковую строку положительное число длинной >8 символов ("+shortBarCode+") и инициировать поиск");
        searchProductPage.enterTextInSearchField(shortBarCode);
        searchProductPage.shouldProductCardsContainText(shortBarCode);
    }
    @Test(description = "C22846686 Мой магазин. Выбор фильтров каждого блока фильтров")
    public void testC22846686() throws Exception{

        LoginPage loginPage = new LoginPage(context);
        UserData seller = new UserData(EnvConstants.BASIC_USER_NAME,EnvConstants.BASIC_USER_PASS);
        LocalDate avsDate = LocalDate.of(2019, 12,5);
        String supplierSearchContext = "123";
        Color expectedColor = MagMobCheckBox.getActiveGreenColor();

        // Pre-conditions
        loginPage.loginInAndGoTo(seller, LoginPage.SALES_SECTION);
        SalesPage salesPage = new SalesPage(context);
        SearchProductPage searchProductPage = salesPage.clickSearchBar();
        SuppliersSearchPage suppliersSearchPage = new SuppliersSearchPage(context);
        MyShopFilterPage filterPage = searchProductPage.goToFilterPage();

        // Step 1
        log.step("выбрать одну из гамм");
        filterPage.choseGammaFilter(filterPage.GAMMA+" A");
        filterPage.shouldFilterHasBeenChosen(filterPage.GAMMA+" A", expectedColor);

        // Step 2
        log.step("выбрать один из топов");
        filterPage.choseTopFilter();
        filterPage.shouldFilterHasBeenChosen(filterPage.TOP,expectedColor);

        // Step 3
        log.step("выбрать 1 из чек-боксов блока с типами товаров");
        filterPage.choseCheckBoxFilter(filterPage.BEST_PRICE);
        filterPage.shouldElementHasBeenSelected(filterPage.BEST_PRICE);

        // Step 4
        log.step("выбрать тип товара");
        filterPage.choseProductType(filterPage.ORDERED_PRODUCT_TYPE);
        filterPage.shouldFilterHasBeenChosen(filterPage.ORDERED_PRODUCT_TYPE,expectedColor);


        // Step 5
        log.step("выбрать 1 поставщика");
        filterPage.goToSuppliersSearchPage();
        suppliersSearchPage.verifyRequiredElements();

        suppliersSearchPage.searchSupplier(supplierSearchContext);
        suppliersSearchPage.shouldCountOfProductsOnPageMoreThan(0);
        suppliersSearchPage.shouldProductCardsContainText(supplierSearchContext);
        suppliersSearchPage.verifyElementIsSelected(supplierSearchContext);
        suppliersSearchPage.applyChosenSupplier();

        // Step 6
        log.step("выбрать дату авс");
        filterPage.choseAvsDate(avsDate);
        filterPage.shouldElementHasBeenSelected(filterPage.AVS);

        // Step 7
        log.step("подтвердить примененные фильтры");
        filterPage.applyChosenFilters();
        searchProductPage.verifyRequiredElements();

        // TODO добавить проверку респонса на соответсвие полученных сущностей выбранным фильтрам
    }

    @Test(description = "C22789209 Вся гамма ЛМ. Выбор фильтров каждого раздела")
    public void testC22789209()throws Exception{
        LoginPage loginPage = new LoginPage(context);
        UserData seller = new UserData(EnvConstants.BASIC_USER_NAME,EnvConstants.BASIC_USER_PASS);
        LocalDate avsDate = LocalDate.of(2019, 12,5);
        Color expectedColor = MagMobCheckBox.getActiveGreenColor();

        // Pre-conditions
        loginPage.loginInAndGoTo(seller, LoginPage.SALES_SECTION);
        SalesPage salesPage = new SalesPage(context);
        SearchProductPage searchProductPage = salesPage.clickSearchBar();
        FilterPage filterPage = searchProductPage.goToFilterPage();

        // Step 1
        log.step("выбрать овальный чек-бокс \"Вся гамма ЛМ\"");
        filterPage.switchFiltersFrame(filterPage.ALL_GAMMA_FRAME_TYPE);
        filterPage.shouldFilterHasBeenChosen(filterPage.ALL_GAMMA_FRAME_TYPE,expectedColor);

        // Step 2
        log.step("выбрать одну из гамм");
        AllGammaFilterPage allGammaFilterPage = new AllGammaFilterPage(context);
        allGammaFilterPage.choseGammaFilter(filterPage.GAMMA+" B");
        allGammaFilterPage.shouldFilterHasBeenChosen(filterPage.GAMMA+" B",expectedColor);

        // Step 3
        log.step("выбрать 1 из чек-боксов блока с типами товаров");
        allGammaFilterPage.choseCheckBoxFilter(allGammaFilterPage.CTM);
        allGammaFilterPage.shouldFilterHasBeenChosen(allGammaFilterPage.CTM,expectedColor);

        // Step 4
        log.step("выбрать тип товара");
        allGammaFilterPage.choseProductType(allGammaFilterPage.ORDERED_PRODUCT_TYPE);
        allGammaFilterPage.shouldFilterHasBeenChosen(allGammaFilterPage.ORDERED_PRODUCT_TYPE,expectedColor);

        // Step 5
        log.step("выбрать дату авс");
        allGammaFilterPage.choseAvsDate(avsDate);
        allGammaFilterPage.shouldElementHasBeenSelected(allGammaFilterPage.AVS);

        // Step 6
        log.step("подтвердить примененные фильтры");
        allGammaFilterPage.applyChosenFilters();
        searchProductPage.verifyRequiredElements();

        // TODO добавить проверку респонса на соответсвие полученных сущностей выбранным фильтрам
    }

    @Test(description = "C22789172 На поисковой запрос не вернулись результаты")
    public void testC22789172()throws Exception {
        LoginPage loginPage = new LoginPage(context);
        UserData seller = new UserData(EnvConstants.BASIC_USER_NAME, EnvConstants.BASIC_USER_PASS);
        Color expectedGreenColor = MagMobCheckBox.getActiveGreenColor();
        Color expectedWhiteColor = MagMobCheckBox.getInactiveWhiteColor();

        // Pre-conditions
        loginPage.loginInAndGoTo(seller, LoginPage.SALES_SECTION);
        SalesPage salesPage = new SalesPage(context);
        SearchProductPage searchProductPage = salesPage.clickSearchBar();
        String byName = "А13";

        // Step 1
        log.step("Ввести в поле поиска значение, результат поиска по которому не вернется");
        searchProductPage.enterTextInSearchField(byName);
        searchProductPage.shouldNotFoundMsgBeDisplayed(byName);

        // Step 2
        log.step("выбрать более 1 фильтра и нажать \"Показать товары\"");
        MyShopFilterPage myShopFilterPage = searchProductPage.goToFilterPage();
        myShopFilterPage.scrollHorizontalWidget(myShopFilterPage.GAMMA, myShopFilterPage.GAMMA+" ET");
        myShopFilterPage.choseGammaFilter(myShopFilterPage.GAMMA+" ET");
        myShopFilterPage.shouldFilterHasBeenChosen(myShopFilterPage.GAMMA+" ET",expectedGreenColor);
        myShopFilterPage.applyChosenFilters();

        searchProductPage.verifyRequiredElements();
        searchProductPage.shouldNotFoundMsgBeDisplayed(byName);
        searchProductPage.shouldDiscardAllFiltersBtnBeDisplayed();

        // Step 3
        log.step("Нажать на кнопку \"Cбросить фильтры\"");
        searchProductPage.discardFilters();
        searchProductPage.shouldNotDiscardAllFiltersBtnBeDisplayed();
        searchProductPage.shouldNotFoundMsgBeDisplayed(byName);

        // Step 4
        log.step("перейти в фильтры");
        searchProductPage.goToFilterPage();
        myShopFilterPage.scrollHorizontalWidget(myShopFilterPage.GAMMA, myShopFilterPage.GAMMA+" ET");
        myShopFilterPage.shouldFilterHasBeenChosen(myShopFilterPage.GAMMA+" ET",expectedWhiteColor);

    }
}
