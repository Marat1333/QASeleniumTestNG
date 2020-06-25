package com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.elements.MagMobGreenSubmitButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ReviewsPage;
import com.leroy.magmobile.ui.pages.sales.product_card.SimilarProductsPage;
import com.leroy.magmobile.ui.pages.sales.product_card.SpecificationsPage;
import io.qameta.allure.Step;

public class ProductPricesQuantitySupplyPage extends CommonMagMobilePage {

    @AppFindBy(accessibilityId = "BackButton")
    Button backBtn;

    @AppFindBy(text = "ПОСТАВКИ")
    MagMobGreenSubmitButton supplyBtn;

    public enum Tabs{
        PRICES("ЦЕНЫ"),
        STOCKS("ЗАПАС"),
        SUPPLY("ПОСТАВКИ");

        private String name;

        Tabs(String name){
            this.name=name;
        }

        public String getName() {
            return name;
        }
    }

    @Step("Перейти назад")
    public ProductDescriptionPage goBack(){
        backBtn.click();
        return new ProductDescriptionPage();
    }

    @Step("Перейти во вкладку")
    public <T> T switchTab(Tabs tabs) {
        Element element;
        switch (tabs) {
            case PRICES:
                element = E(Tabs.PRICES.getName());
                element.click();
                return (T) new PricesPage();
            case STOCKS:
                element = E(Tabs.STOCKS.getName());
                element.click();
                return (T) new StocksPage();
            case SUPPLY:
                element = E(Tabs.SUPPLY.getName());
                element.click();
                return (T) new SuppliesPage();
            default:
                throw new IllegalArgumentException("Unknown argument");
        }
    }

    public void shouldNotSupplyBtnBeDisplayed() {
        anAssert.isFalse(supplyBtn.isVisible(), "Раздел \"Поставки\" не отображен");
    }
}
