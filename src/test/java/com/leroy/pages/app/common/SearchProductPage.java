package com.leroy.pages.app.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.pages.app.common.modal.SortModal;
import com.leroy.pages.app.sales.AddProductPage;
import com.leroy.pages.app.sales.widget.SearchProductCardWidget;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class SearchProductPage extends BaseAppPage {

    public SearchProductPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "BackButton", metaName = "Кнопка назад")
    private Element backBtn;

    @AppFindBy(accessibilityId = "Button", metaName = "Кнопка для сканирования штрихкода")
    private Element scanBarcodeBtn;

    @AppFindBy(accessibilityId = "ScreenTitle-CatalogComplexSearchStore", metaName = "Поле ввода текста для поиска")
    private EditBox searchField;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']//android.view.ViewGroup[android.widget.ImageView]",
            clazz = SearchProductCardWidget.class)
    private ElementList<SearchProductCardWidget> productCards;

    @AppFindBy(text = "Фильтр")
    Element filter;

    @AppFindBy(xpath = "(//android.view.ViewGroup[descendant::android.widget.TextView[@text='Фильтр']]/preceding-sibling::android.view.ViewGroup[descendant::android.widget.TextView])[2]//android.widget.TextView",
            metaName = "Выбранная номенклатура")
    Element nomenclature;

    @AppFindBy(xpath = "//android.view.ViewGroup[preceding-sibling::android.view.ViewGroup[2][ancestor::android.view.ViewGroup[@content-desc=\"ScreenContent\"]]]")
    Element sort;

    Element discardAllFiltersBtn = E("contains(СБРОСИТЬ ФИЛЬТРЫ)");

    private final String notFoundMsg = "//*[contains(@text, 'Поиск «%s» не дал результатов')]";

    @Override
    public void waitForPageIsLoaded() {
        searchField.waitForVisibility();
    }

    // ---------------- Action Steps -------------------------//

    @Step("Сбросить фильтры, инициировав скрипт со страницы поиска")
    public void discardFilters() {
        discardAllFiltersBtn.click();
    }

    @Step("Введите {text} в поле поиска товара")
    public SearchProductPage enterTextInSearchField(String text) {
        searchField.clearFillAndSubmit(text);
        waitForProgressBarIsVisible();
        waitForProgressBarIsInvisible();
        return this;
    }

    @Step("Найдите и перейдите в карточку товара {text}")
    public AddProductPage searchProductAndSelect(String text) throws Exception {
        searchField.clearFillAndSubmit(text);
        waitForProgressBarIsVisible();
        waitForProgressBarIsInvisible();
        if (searchField.isVisible()) {
            anAssert.isTrue(productCards.getCount() > 0,
                    String.format("Не найден ни один товар по ключевому слову '%s'", text));
            productCards.get(0).click();
        }
        return new AddProductPage(context);
    }

    @Step("Перейти в окно выбора единицы номенклатуры")
    public NomenclatureSearchPage goToNomenclatureWindow() {
        nomenclature.click();
        return new NomenclatureSearchPage(context);
    }

    @Step("Перейти на страницу выбора фильтров")
    public MyShopFilterPage goToFilterPage() {
        filter.click();
        return new MyShopFilterPage(context);
    }

    @Step("Открыть окно сортировки")
    public SortModal openSortPage() {
        sort.click();
        return new SortModal(context);
    }

    // ---------------- Verifications ----------------------- //

    @Override
    public SearchProductPage verifyRequiredElements() {
        softAssert.isElementVisible(backBtn);
        softAssert.isElementVisible(scanBarcodeBtn);
        softAssert.isElementVisible(searchField);
        softAssert.verifyAll();
        return this;
    }

    public void shouldNotFoundMsgBeDisplayed(String value) {
        Element element = new Element(driver, By.xpath(String.format(notFoundMsg, value)));
        anAssert.isTrue(element.isVisible(),
                "Поиск по запросу " + value + " не должен вернуть результатов. Видно соответствующее сообщение");
    }

    public void shouldDiscardAllFiltersBtnBeDisplayed() {
        discardAllFiltersBtn.waitForVisibility();
        anAssert.isTrue(discardAllFiltersBtn.isVisible(), "Кнопка \"Сбросить фильтры\" отображена");
    }

    public void shouldNotDiscardAllFiltersBtnBeDisplayed() {
        discardAllFiltersBtn.waitForInvisibility();
        anAssert.isFalse(discardAllFiltersBtn.isVisible(), "Кнопка \"Сбросить фильтры\" не отображена");
    }

    public SearchProductPage shouldCountOfProductsOnPageMoreThan(int count) {
        anAssert.isTrue(productCards.getCount() > count,
                "Кол-во товаров на экране должно быть больше " + count);
        return this;
    }

    public void shouldProductCardsContainText(String text) {
        String[] searchWords = null;
        if (text.contains(" "))
            searchWords = text.split(" ");
        anAssert.isFalse(E("contains(не найдено)").isVisible(), "Должен быть найден хотя бы один товар");
        anAssert.isTrue(productCards.getCount() > 1,
                "Ничего не найдено для " + text);
        for (SearchProductCardWidget card : productCards) {
            if (searchWords != null) {
                for (String each : searchWords) {
                    anAssert.isTrue(card.getName().toLowerCase().contains(each.toLowerCase()),
                            String.format("Товар с кодом %s не содержит текст %s", card.getNumber(), text));
                }
            } else {
                anAssert.isTrue(card.getBarCode().contains(text) ||
                                card.getName().contains(text) || card.getNumber().contains(text),
                        String.format("Товар с кодом %s не содержит текст %s", card.getNumber(), text));
            }
        }
    }

    public SearchProductPage shouldSelectedNomenclatureIs(String text) {
        anAssert.isElementTextEqual(nomenclature, text);
        return this;
    }

    public SearchProductPage shouldProductCardContainAllRequiredElements(int index) throws Exception {
        anAssert.isFalse(productCards.get(index).getBarCode().isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустой штрихкод", index));
        anAssert.isFalse(productCards.get(index).getNumber().isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустой номер", index));
        anAssert.isFalse(productCards.get(index).getName().isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустое название", index));
        anAssert.isFalse(productCards.get(index).getPrice().isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустую цену", index));
        anAssert.isEquals(productCards.get(index).getPriceLbl(), "за штуку",
                String.format("Карточка под индексом %s должна иметь примечание 'за штуку'", index));
        anAssert.isFalse(productCards.get(index).getQuantity().isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустое кол-во", index));
        anAssert.isEquals(productCards.get(index).getQuantityLbl(), "доступно",
                String.format("Карточка под индексом %s должна иметь примечание 'доступно'", index));
        anAssert.isFalse(productCards.get(index).getQuantityType().isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустой тип кол-ва", index));
        return this;
    }

}
