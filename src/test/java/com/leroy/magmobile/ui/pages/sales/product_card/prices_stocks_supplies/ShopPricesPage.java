package com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.api.data.shops.ShopData;
import com.leroy.magmobile.ui.models.product_card.ShopCardData;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.product_card.widgets.ShopPriceInfoWidget;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;

import java.util.List;

public class ShopPricesPage extends CommonMagMobilePage {
    @AppFindBy(text = "Цены в магазинах")
    private Element title;

    @AppFindBy(accessibilityId = "BackCloseModal")
    Button backBtn;

    @AppFindBy(accessibilityId = "Button")
    Button openInputBtn;

    @AppFindBy(xpath = "//android.widget.EditText")
    EditBox input;

    private AndroidScrollView<ShopCardData> shopCardsScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//*[contains(@text,'км')]/../*[1]", ShopPriceInfoWidget.class);

    @Override
    protected void waitForPageIsLoaded() {
        title.waitForVisibility();
    }

    protected AndroidScrollView<ShopCardData> getShopCardsScrollView() {
        return shopCardsScrollView;
    }

    @Step("Ввести в поисковую строку значение для поиска магазина")
    public void searchShopBy(String criterion) {
        openInputBtn.click();
        input.waitForVisibility();
        input.clearAndFill(criterion);
        E("//android.widget.ScrollView//android.widget.TextView").waitUntilTextContains(criterion);
    }

    @Step("Перейти на предыдущую страницу")
    public ProductPricesQuantitySupplyPage goToPreviousPage() {
        backBtn.click();
        return new ProductPricesQuantitySupplyPage();
    }

    @Step("Проверить цены в магазинах")
    public ShopPricesPage shouldShopPricesAreCorrect(List<ShopData> data) {
        List<ShopCardData> shopData = shopCardsScrollView.getFullDataList();
        for (int i = 0; i < shopData.size(); i++) {
            ShopCardData uiData = shopData.get(i);
            ShopData apiData = data.get(i);
            softAssert.isEquals(uiData.getId(), apiData.getId() + " " + apiData.getName(), "id and name");
            softAssert.isContainsIgnoringCase(uiData.getPrice().replaceAll(",", "."), ParserUtil.prettyDoubleFmt(apiData.getPriceAndStock().getPrice()), "price");
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что карточки магазинов содержат поисковой критерий")
    public void shouldShopCardsContainsSearchCriterion(String criterion) {
        List<ShopCardData> shopData = getShopCardsScrollView().getFullDataList();
        for (ShopCardData data : shopData) {
            anAssert.isContainsIgnoringCase(data.getId(), criterion, "id and name");
        }
    }
}
