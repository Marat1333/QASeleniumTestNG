package com.leroy.magmobile.ui.pages.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.models.ProductCardData;
import com.leroy.models.TextViewData;
import com.leroy.magmobile.ui.pages.common.modal.SortModal;
import com.leroy.magmobile.ui.pages.sales.AddProductPage;
import com.leroy.magmobile.ui.pages.sales.SalesPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.widget.SearchProductCardWidget;
import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.magmobile.data.ProductItemListResponse;
import ru.leroymerlin.qa.core.clients.magmobile.data.ProductItemResponse;

import java.util.ArrayList;
import java.util.List;

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

    private AndroidScrollView<TextViewData> scrollView = new AndroidScrollView<>(driver,
            new CustomLocator(By.xpath("//android.widget.ScrollView"),null,
                    "Виджет прокрутки для истории последних запросов", false));

    private AndroidScrollView<ProductCardData> productMiniCards = new AndroidScrollView<>(driver, new CustomLocator(By.xpath("//android.widget.ScrollView"), null));

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']//android.view.ViewGroup[android.widget.ImageView]",
            clazz = SearchProductCardWidget.class)
    private ElementList<SearchProductCardWidget> productCards;

    @AppFindBy(text = "Фильтр")
    Element filter;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']/android.view.ViewGroup[2]/android.view.ViewGroup[1]//android.widget.TextView",
            metaName = "Выбранная номенклатура")
    Element nomenclature;

    @AppFindBy(xpath = "//android.view.ViewGroup[preceding-sibling::android.view.ViewGroup[2][ancestor::android.view.ViewGroup[@content-desc=\"ScreenContent\"]]]")
    Element sort;

    @AppFindBy(text = "Ты пока ничего не искал(а)")
    Element firstSearchMsg;

    Element discardAllFiltersBtn = E("contains(СБРОСИТЬ ФИЛЬТРЫ)");

    private final String NOT_FOUND_MSG_XPATH = "//*[contains(@text, 'Поиск «%s» не дал результатов')]";

    @Override
    public void waitForPageIsLoaded() {
        searchField.waitForVisibility();
        waitForProgressBarIsInvisible();
    }

    // ---------------- Action Steps -------------------------//

    @Step("Перейти на главную страницу 'Документы продажи'")
    public SalesPage backToSalesPage() {
        backBtn.click();
        return new SalesPage(context);
    }

    @Step("Ввести поисковой запрос со случайным текстом {value} раз и инициировать поиск")
    public List<String> createSearchHistory(int value) {
        List<String> searchHistory = new ArrayList<>();
        String tmp = RandomStringUtils.randomAlphanumeric(1);
        for (int i = 0; i < value; i++) {
            searchField.fill(tmp)
                    .submit();
            searchHistory.add(tmp);
            tmp = tmp + RandomStringUtils.randomAlphanumeric(1);
        }
        return searchHistory;
    }

    @Step("Сбросить фильтры, инициировав скрипт со страницы поиска")
    public void discardFilters() {
        discardAllFiltersBtn.click();
    }

    @Step("Введите {text} в поле поиска товара и выполните поиск")
    public SearchProductPage enterTextInSearchFieldAndSubmit(String text) {
        searchField.clearFillAndSubmit(text);
        waitForProgressBarIsVisible();
        waitForProgressBarIsInvisible();
        return this;
    }

    @Step("Введите {text} в поле поиска товара")
    public SearchProductPage enterTextInSearchField(String text) {
        searchField.clearAndFill(text);
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

    @Step("Перейти в {index} карточку товара")
    public ProductDescriptionPage selectProductCardByIndex(int index) throws Exception {
        anAssert.isTrue(productCards.getCount() > index, "Не найдена " + index + " по счету карточка товара");
        productCards.get(index).click();
        return new ProductDescriptionPage(context);
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

    public SearchProductPage verifyRequiredElements() {
        softAssert.isElementVisible(backBtn);
        softAssert.isElementVisible(scanBarcodeBtn);
        softAssert.isElementVisible(searchField);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверяем, что список последних поисковых запросов такой: {expectedList}")
    public SearchProductPage shouldSearchHistoryListIs(List<String> expectedList) throws Exception {
        List<String> actualStringList = scrollView.getFullDataAsStringList();
        anAssert.isEquals(actualStringList, expectedList, "Ожидается следующий список поисковых запросов: %s");
        return this;
    }

    @Step("Проверяем, что список последних поисковых запросов содержит {searchPhrase}")
    public SearchProductPage verifySearchHistoryContainsSearchPhrase(String searchPhrase) throws Exception {
        List<String> containsVisibleSearchHistory = scrollView.getFullDataAsStringList();
        anAssert.isFalse(containsVisibleSearchHistory.size() == 0, "История поиска - пустая");
        for (String tmp : containsVisibleSearchHistory) {
            softAssert.isTrue(tmp.contains(searchPhrase), "Каждое совпадение должно содержать поисковую строку");
        }
        softAssert.isEquals(containsVisibleSearchHistory.get(containsVisibleSearchHistory.size() - 1), searchPhrase,
                "Последний элемент истории поиска должен полностью совпадать с поисковой фразой");
        softAssert.verifyAll();
        return this;
    }

    public void shouldFirstSearchMsgBeDisplayed() {
        anAssert.isTrue(firstSearchMsg.isVisible(), "Должно быть отображено сообщение о первом поиске");
    }

    public void shouldNotFoundMsgBeDisplayed(String value) {
        Element element = new Element(driver, By.xpath(String.format(NOT_FOUND_MSG_XPATH, value)));
        anAssert.isTrue(element.isVisible(), "Поиск по запросу " + value + " не вернул результатов");
    }

    @Step("Проверить, что карточки товаров имеют соответсвующий вид для фильтра 'Вся гамма ЛМ'")
    public void verifyProductCardsHaveAllGammaView() throws Exception {
        anAssert.isFalse(E("за штуку").isVisible(), "Карточки товаров не должны содержать цену");
        anAssert.isFalse(E("доступно").isVisible(), "Карточки товаров не должны содержать доступное кол-во");
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
                            String.format("Товар с кодом %s не содержит текст %s", card.getLmCode(), text));
                }
            } else {
                anAssert.isTrue(card.getBarCode().contains(text) ||
                                card.getName().contains(text) || card.getLmCode().contains(text),
                        String.format("Товар с кодом %s не содержит текст %s", card.getLmCode(), text));
            }
        }
    }

    public SearchProductPage shouldSelectedNomenclatureIs(String text, boolean strictEqual) throws Exception {
        if (strictEqual) {
            anAssert.isElementTextEqual(nomenclature, text);
        }else {
            anAssert.isElementTextContains(nomenclature, text);
        }
        return this;
    }

    public SearchProductPage shouldProductCardContainAllRequiredElements(int index) throws Exception {
        anAssert.isFalse(productCards.get(index).getBarCode().isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустой штрихкод", index));
        anAssert.isFalse(productCards.get(index).getLmCode().isEmpty(),
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

    // API verifications
    public SearchProductPage shouldResponceEqualsContent(Response<ProductItemListResponse> response) throws Exception{
        List<ProductItemResponse> productData = response.asJson().getItems();
        List<ProductCardData> productCardData = productMiniCards.getFullDataList();

        for (ProductItemResponse item: productData){

        }
        return this;
    }

}
