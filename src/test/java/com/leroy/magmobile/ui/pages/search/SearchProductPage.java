package com.leroy.magmobile.ui.pages.search;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.models.CardWidgetData;
import com.leroy.magmobile.models.TextViewData;
import com.leroy.magmobile.models.search.ProductCardData;
import com.leroy.magmobile.models.search.ServiceCardData;
import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.sales.AddProductPage;
import com.leroy.magmobile.ui.pages.sales.MainProductAndServicesPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.widget.SearchProductAllGammaCardWidget;
import com.leroy.magmobile.ui.pages.sales.widget.SearchProductCardWidget;
import com.leroy.magmobile.ui.pages.sales.widget.SearchServiceCardWidget;
import com.leroy.magmobile.ui.pages.search.modal.SortPage;
import com.leroy.umbrella_extension.magmobile.data.ProductItemListResponse;
import com.leroy.umbrella_extension.magmobile.data.ProductItemResponse;
import com.leroy.umbrella_extension.magmobile.data.ServiceItemListResponse;
import com.leroy.umbrella_extension.magmobile.data.ServiceItemResponse;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SearchProductPage extends CommonMagMobilePage {

    public SearchProductPage(Context context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "back", metaName = "Кнопка назад")
    private Element backBtn;

    @AppFindBy(accessibilityId = "ScannerBtn", metaName = "Кнопка для сканирования штрихкода")
    private Element scanBarcodeBtn;

    @AppFindBy(accessibilityId = "ScreenTitle-CatalogComplexSearchStore", metaName = "Поле ввода текста для поиска")
    private EditBox searchField;

    private AndroidScrollView<TextViewData> searchHistoryScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR);

    private AndroidScrollView<ProductCardData> productCardsScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//android.view.ViewGroup[contains(@content-desc,'productListCard')]", SearchProductCardWidget.class);

    private AndroidScrollView<ServiceCardData> serviceCardsScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//android.view.ViewGroup[contains(@content-desc,'serviceListCard')]", SearchServiceCardWidget.class);

    private AndroidScrollView<ProductCardData> allGammaProductCardsScrollView = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR,
            ".//android.view.ViewGroup[contains(@content-desc,'productListCard')]", SearchProductAllGammaCardWidget.class);

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']//android.view.ViewGroup[android.widget.ImageView]",
            clazz = SearchProductCardWidget.class)
    private ElementList<SearchProductCardWidget> productCards;

    @AppFindBy(text = "Фильтр")
    Element filter;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc='FilterBtn']/ancestor::android.view.ViewGroup[2]" +
            "/following-sibling::android.view.ViewGroup//android.widget.TextView")
    Element filterCounter;

    @AppFindBy(xpath = "//android.widget.TextView[@content-desc=\"Nomenclature\"]",
            metaName = "Выбранная номенклатура")
    Element nomenclature;

    @AppFindBy(accessibilityId = "SortBtn")
    Element sort;

    @AppFindBy(text = "Ты пока ничего не искал(а)")
    Element firstSearchMsg;

    @AppFindBy(text = "Ничего не найдено")
    Element notFoundMsgLbl;

    Element discardAllFiltersBtn = E("contains(СБРОСИТЬ ФИЛЬТРЫ)");

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"Button\"]/android.view.ViewGroup")
    Element clearTextInputBtn;

    private final String NOT_FOUND_MSG_XPATH = "//*[contains(@text, 'Поиск «%s» не дал результатов')]";

    private final String DEFAULT_SEARCH_UNPUT_TEXT = "ЛМ, название или штрихкод";

    public enum CardType {
        COMMON, // Обычная
        ALL_GAMMA, // Вся гамма ЛМ
        SERVICE // Услуга
    }

    @Override
    public void waitForPageIsLoaded() {
        searchField.waitForVisibility();
        backBtn.waitForVisibility();
        waitUntilProgressBarIsInvisible();
    }

    // ---------------- Action Steps -------------------------//

    @Step("Перейти на главную страницу 'Документы продажи'")
    public MainProductAndServicesPage backToSalesPage() {
        backBtn.click();
        return new MainProductAndServicesPage(context);
    }

    @Step("Ввести поисковой запрос со случайным текстом {value} раз и инициировать поиск")
    public List<String> createSearchHistory(int value) {
        List<String> searchHistory = new ArrayList<>();
        String tmp = "1";
        for (int i = 0; i < value; i++) {
            searchField.click();
            searchField.fill(tmp);
            searchField.submit();
            searchHistory.add(tmp);
            tmp = tmp + "1";
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
        waitUntilProgressBarIsVisible();
        waitUntilProgressBarIsInvisible();
        //TODO Добавить проверку на наличие ошибки ответа сервака
        return this;
    }

    @Step("Введите {text} в поле поиска товара")
    public SearchProductPage enterTextInSearchField(String text) {
        searchField.clearAndFill(text);
        hideKeyboard();
        return this;
    }

    @Step("Инициируем поисковой запрос через поисковую строку")
    public SearchProductPage submitSearch() {
        searchField.submit();
        hideKeyboard();
        return new SearchProductPage(context);
    }

    @Step("Очистить поисковой инпут")
    public SearchProductPage clearSearchInput() {
        clearTextInputBtn.click();
        return new SearchProductPage(context);
    }

    @Step("Найдите и перейдите в карточку товара {text}")
    public AddProductPage searchProductAndSelect(String text) throws Exception {
        searchField.clearFillAndSubmit(text);
        waitUntilProgressBarIsVisible();
        waitUntilProgressBarIsInvisible();
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
        index--;
        productCards.get(index).click();
        return new ProductDescriptionPage(context);
    }

    @Step("Перейти в окно выбора единицы номенклатуры")
    public NomenclatureSearchPage goToNomenclatureWindow() {
        nomenclature.click();
        return new NomenclatureSearchPage(context);
    }

    @Step("Перейти на страницу выбора фильтров")
    public FilterPage goToFilterPage() {
        filter.click();
        return new FilterPage(context);
    }

    @Step("Открыть окно сортировки")
    public SortPage openSortPage() {
        sort.click();
        return new SortPage(context);
    }

    // ---------------- Verifications ----------------------- //

    @Step("Проверить, что страница поиска товаров и услуг отображается корректно")
    public SearchProductPage verifyRequiredElements() {
        softAssert.isElementVisible(backBtn);
        softAssert.isElementVisible(searchField);
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что счетчик фильтров равен значению {value}")
    public SearchProductPage shouldFilterCounterEquals(int value) {
        if (value == 0) {
            anAssert.isElementNotVisible(filterCounter);
        } else if (value > 0) {
            anAssert.isElementTextEqual(filterCounter, String.valueOf(value));
        } else {
            throw new IllegalArgumentException("value should be more than -1");
        }
        return this;
    }

    @Step("Проверяем, что появилась кнопка очистки инпута поисковой строки")
    public SearchProductPage verifyClearTextInputBtnIsVisible() {
        anAssert.isElementVisible(clearTextInputBtn);
        return this;
    }

    @Step("Проверить, что кнопка 'Сканировать бар код' отображается")
    public SearchProductPage shouldScannerBtnIsVisible() {
        String pageSource = getPageSource();
        if (searchField.getText(pageSource).equals(DEFAULT_SEARCH_UNPUT_TEXT)) {
            anAssert.isElementVisible(scanBarcodeBtn, pageSource);
        } else {
            anAssert.isElementNotVisible(scanBarcodeBtn, pageSource);
        }
        return this;
    }

    @Step("Проверяем, что список последних поисковых запросов такой: {expectedList}")
    public SearchProductPage shouldSearchHistoryListIs(List<String> expectedList) throws Exception {
        List<String> actualStringList = searchHistoryScrollView.getFullDataAsStringList();
        anAssert.isEquals(actualStringList, expectedList, "Ожидается следующий список поисковых запросов: %s");
        return this;
    }

    @Step("Проверяем, что список последних поисковых запросов содержит {searchPhrase}")
    public SearchProductPage verifySearchHistoryContainsSearchPhrase(String searchPhrase) throws Exception {
        List<String> containsVisibleSearchHistory = searchHistoryScrollView.getFullDataAsStringList();
        anAssert.isFalse(containsVisibleSearchHistory.size() == 0, "История поиска - пустая");
        for (String tmp : containsVisibleSearchHistory) {
            softAssert.isTrue(tmp.contains(searchPhrase), "Каждое совпадение должно содержать поисковую строку");
        }
        softAssert.isEquals(containsVisibleSearchHistory.get(containsVisibleSearchHistory.size() - 1), searchPhrase,
                "Последний элемент истории поиска должен полностью совпадать с поисковой фразой");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что сообщение о первом поиске отображено")
    public void shouldFirstSearchMsgBeDisplayed() {
        anAssert.isTrue(firstSearchMsg.isVisible(), "Должно быть отображено сообщение о первом поиске");
    }

    @Step("сообщение о первом поиске не отображено")
    public void shouldNotFirstSearchMsgBeDisplayed() {
        anAssert.isFalse(firstSearchMsg.isVisible(), "Cообщение о первом поиске не должно быть отображено");
    }

    @Step("Проверить, что сообщение о том, что ничего не найдено - отображено")
    public void shouldNotFoundMsgBeDisplayed(String value) {
        Element element = new Element(driver, By.xpath(String.format(NOT_FOUND_MSG_XPATH, value)));
        anAssert.isTrue(element.isVisible(), "Поиск по запросу " + value + " не вернул результатов");
    }

    @Step("Проверить, что карточки товаров имеют соответсвующий вид для фильтра 'Вся гамма ЛМ'")
    public void verifyProductCardsHaveAllGammaView() {
        anAssert.isFalse(E("за штуку").isVisible(), "Карточки товаров не должны содержать цену");
        anAssert.isFalse(E("доступно").isVisible(), "Карточки товаров не должны содержать доступное кол-во");
    }

    @Step("Проверить, что Кнопка сброса фильтров отображена")
    public void shouldDiscardAllFiltersBtnBeDisplayed() {
        discardAllFiltersBtn.waitForVisibility();
        anAssert.isTrue(discardAllFiltersBtn.isVisible(), "Кнопка \"Сбросить фильтры\" отображена");
    }

    @Step("Проверить, что Кнопка сброса фильтров не отображена")
    public void shouldNotDiscardAllFiltersBtnBeDisplayed() {
        discardAllFiltersBtn.waitForInvisibility();
        anAssert.isFalse(discardAllFiltersBtn.isVisible(), "Кнопка \"Сбросить фильтры\" не отображена");
    }

    @Step("Проверить, что картчоек товара больше {count}")
    public SearchProductPage shouldCountOfProductsOnPageMoreThan(int count) {
        anAssert.isTrue(productCards.getCount() > count,
                "Кол-во товаров на экране должно быть больше " + count);
        return this;
    }

    @Step("Проверить, что все карточки с продуктами содержат текст: {text}")
    public SearchProductPage shouldProductCardsContainText(String text) {
        for (SearchProductCardWidget card : productCards) {
            anAssert.isTrue(card.getBarCode(true).contains(text) ||
                            card.getName().contains(text) || card.getLmCode(false).contains(text),
                    String.format("Товар с кодом %s не содержит текст %s", card.getLmCode(false), text));
        }
        return this;
    }

    @Step("Проверить, что Кнопка номенклатуры содержит текст {text}")
    public SearchProductPage shouldSelectedNomenclatureIs(String text, boolean strictEqual) throws Exception {
        if (strictEqual) {
            anAssert.isElementTextEqual(nomenclature, text);
        } else {
            anAssert.isElementTextContains(nomenclature, text);
        }
        return this;
    }

    @Step("Проверить, что Карточка товара содержит все элементы")
    public SearchProductPage shouldProductCardContainAllRequiredElements(int index) throws Exception {
        index--;
        String ps = getPageSource();
        anAssert.isFalse(productCards.get(index).getBarCode(true, ps).isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустой штрихкод", index));
        anAssert.isFalse(productCards.get(index).getLmCode(true, ps).isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустой номер", index));
        anAssert.isFalse(productCards.get(index).getName(ps).isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустое название", index));
        anAssert.isFalse(productCards.get(index).getPrice(ps).isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустую цену", index));
        anAssert.isEquals(productCards.get(index).getPriceLbl(ps), "за штуку",
                String.format("Карточка под индексом %s должна иметь примечание 'за штуку'", index));
        anAssert.isFalse(productCards.get(index).getQuantity(ps).isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустое кол-во", index));
        anAssert.isEquals(productCards.get(index).getQuantityLbl(ps), "доступно",
                String.format("Карточка под индексом %s должна иметь примечание 'доступно'", index));
        anAssert.isFalse(productCards.get(index).getPriceUnit(ps).isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустой тип кол-ва", index));
        return this;
    }

    @Step("Проверить, что карточки с товарами отсортированы {sortType}")
    public SearchProductPage shouldProductCardsBeSorted(String sortType, CardType type, int howManyProductsNeedToVerify) throws Exception {
        List<ProductCardData> dataList;
        switch (type) {
            case COMMON:
                dataList = productCardsScrollView.getFullDataList(howManyProductsNeedToVerify);
                break;
            case ALL_GAMMA:
                dataList = allGammaProductCardsScrollView.getFullDataList(howManyProductsNeedToVerify);
                break;
            default:
                throw new Exception("Incorrect CardType");
        }
        anAssert.isFalse(dataList.size() == 0, "На странице отсутствуют карточки товаров");
        List<ProductCardData> expectedSortedList = new ArrayList<>(dataList);

        switch (sortType) {
            case SortPage.SORT_BY_AVAILABLE_STOCK_ASC:
                if (type.equals(CardType.COMMON)) {
                    expectedSortedList.sort(Comparator.comparingDouble(d -> d.getAvailableQuantity()));
                    break;
                } else {
                    throw new Exception("Incorrect CardType for " + sortType);
                }

            case SortPage.SORT_BY_AVAILABLE_STOCK_DESC:
                if (type.equals(CardType.COMMON)) {
                    expectedSortedList.sort((d1, d2) ->
                            (int) (d2.getAvailableQuantity() - d1.getAvailableQuantity()));
                    break;
                } else {
                    throw new Exception("Incorrect CardType for " + sortType);
                }
            case SortPage.SORT_BY_LM_ASC:
                expectedSortedList.sort(Comparator.comparingInt(d -> Integer.parseInt(d.getLmCode())));
                break;
            case SortPage.SORT_BY_LM_DESC:
                expectedSortedList.sort((d1, d2) -> Integer.parseInt(d2.getLmCode()) - Integer.parseInt(d1.getLmCode()));
                break;
            default:
                anAssert.isTrue(false,
                        "Не предусмотрен метод для проверки сортировки с типом: " + sortType);
                break;
        }
        anAssert.isEquals(dataList, expectedSortedList, "Элементы отсортированы некорректно");
        return this;
    }

    @Step("Проверить, что карточка товара содержит текст {text}")
    public void shouldCardsContainText(String text, CardType type, int howManyProductsNeedToVerify) throws Exception {
        String[] searchWords = null;
        if (text.contains(" "))
            searchWords = text.split(" ");
        anAssert.isFalse(E("contains(не найдено)").isVisible(), "Должен быть найден хотя бы один товар");
        List<? extends CardWidgetData> productCardData;
        switch (type) {
            case COMMON:
                productCardData = productCardsScrollView.getFullDataList(howManyProductsNeedToVerify);
                break;
            case ALL_GAMMA:
                productCardData = allGammaProductCardsScrollView.getFullDataList(howManyProductsNeedToVerify);
                break;
            case SERVICE:
                productCardData = serviceCardsScrollView.getFullDataList(howManyProductsNeedToVerify);
                break;
            default:
                throw new Exception("Incorrect CardType");
        }
        for (CardWidgetData cardData : productCardData) {
            if (text.matches("^\\d+") && text.length() <= 8) {
                if (cardData instanceof ProductCardData) {
                    anAssert.isTrue(((ProductCardData) cardData).getLmCode().contains(text), "ЛМ код товара " + ((ProductCardData) cardData).getLmCode() + " не содрежит критерий поиска " + text);
                } else if (cardData instanceof ServiceCardData) {
                    anAssert.isTrue(((ServiceCardData) cardData).getLmCode().contains(text), "ЛМ код товара " + ((ServiceCardData) cardData).getLmCode() + " не содрежит критерий поиска " + text);
                }
            }
            if (text.matches("^\\d+") && text.length() > 8 && cardData instanceof ProductCardData) {
                anAssert.isTrue(((ProductCardData) cardData).getBarCode().contains(text), "Штрих код товара " + ((ProductCardData) cardData).getBarCode() + " не содрежит критерий поиска " + text);
            }
            if (searchWords != null && text.matches("\\D+")) {
                for (String each : searchWords) {
                    each = each.toLowerCase();
                    if (cardData instanceof ProductCardData) {
                        anAssert.isTrue(((ProductCardData) cardData).getName().toLowerCase().contains(each.toLowerCase()), "Название товара " + ((ProductCardData) cardData).getName() + " не содержит критерий поиска " + text);
                    } else if (cardData instanceof ServiceCardData) {
                        anAssert.isTrue(((ServiceCardData) cardData).getName().toLowerCase().contains(each.toLowerCase()), "Название товара " + ((ServiceCardData) cardData).getName() + " не содержит критерий поиска " + text);
                    }
                }
            } else if (searchWords == null && text.matches("\\D+")) {
                if (cardData instanceof ProductCardData) {
                    anAssert.isTrue(((ProductCardData) cardData).getName().toLowerCase().contains(text.toLowerCase()), "Название товара " + ((ProductCardData) cardData).getName() + " не содержит критерий поиска " + text);
                } else if (cardData instanceof ServiceCardData) {
                    anAssert.isTrue(((ServiceCardData) cardData).getName().toLowerCase().contains(text.toLowerCase()), "Название товара " + ((ServiceCardData) cardData).getName() + " не содержит критерий поиска " + text);
                }
            }
        }
    }

    @Step("Проверить, что на странице отсутсвуют карточки выбранного типа")
    public SearchProductPage shouldNotCardsBeOnPage(CardType type) throws Exception {
        List<? extends CardWidgetData> cardData;
        switch (type) {
            case COMMON:
                cardData = productCardsScrollView.getFullDataList();
                break;
            case ALL_GAMMA:
                cardData = allGammaProductCardsScrollView.getFullDataList();
                break;
            case SERVICE:
                cardData = serviceCardsScrollView.getFullDataList();
                break;
            default:
                throw new Exception("Incorrect CardType");
        }
        anAssert.isTrue(cardData.isEmpty(), "на странице содержаться карточки указанного типа");

        return this;
    }

    // API verifications

    @Step("Проверить, что фронт корректно отобразил ответ от сервера по запросу на catalog product")
    public SearchProductPage shouldCatalogResponseEqualsContent(ProductItemListResponse responseData, CardType type, Integer entityCount) {
        List<ProductItemResponse> productDataListFromResponse = responseData.getItems();
        List<ProductCardData> productCardDataListFromPage;
        switch (type) {
            case COMMON:
                productCardDataListFromPage = productCardsScrollView.getFullDataList(entityCount);
                break;
            case ALL_GAMMA:
                productCardDataListFromPage = allGammaProductCardsScrollView.getFullDataList(entityCount);
                break;
            default:
                throw new IllegalArgumentException("Incorrect CardType");
        }
        anAssert.isEquals(productCardDataListFromPage.size(), productDataListFromResponse.size(),
                "Кол-во записей на странице не соответсвует");
        for (int i = 0; i < productCardDataListFromPage.size(); i++) {
            softAssert.isEquals(productCardDataListFromPage.get(i).getLmCode(),
                    productDataListFromResponse.get(i).getLmCode(), "ЛМ код " + i + "-ого товар отличается");
            softAssert.isEquals(productCardDataListFromPage.get(i).getBarCode(),
                    productDataListFromResponse.get(i).getBarCode(), "Бар код " + i + "-ого товар отличается");
        }
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что фронт корректно отобразил ответ от сервера по запросу на catalog services")
    public SearchProductPage shouldServicesResponceEqualsContent(ServiceItemListResponse responseData, Integer entityCount) {
        List<ServiceItemResponse> serviceData = responseData.getItems();
        List<ServiceCardData> serviceCardDataList = serviceCardsScrollView.getFullDataList(entityCount);
        if (serviceCardDataList.size() != serviceData.size()) {
            throw new AssertionError("Page size param should be equals to maxEntityCount");
        }
        anAssert.isTrue(serviceCardDataList.equals(serviceData), "Товары не совпадают");
        return this;
    }

    /*public SearchProductPage shouldResponceIsNull(Response<ProductItemListResponse> response) {
        anAssert.isTrue(response.asJson().getItems().isEmpty(), "Ответ содержит данные");
        return this;
    }

    public SearchProductPage shouldResponceContainsCorrectData(Response<ProductItemListResponse> response, String criterion) {
        List<ProductItemResponse> productData = response.asJson().getItems();
        if (criterion.startsWith(FilterPage.GAMMA)) {
            String productGamma;
            criterion = criterion.substring(7);
            for (ProductItemResponse eachProduct : productData) {
                productGamma = eachProduct.getGamma();
                anAssert.isEquals(criterion, productGamma, "\"v3 catalog search\" return wrong data by gamma criterion");
            }
        }

        if (criterion.startsWith(MyShopFilterPage.TOP)) {
            String productTop;
            criterion = criterion.substring(5);
            for (ProductItemResponse eachProduct : productData) {
                productTop = String.valueOf(eachProduct.getTop());
                anAssert.isEquals(criterion, productTop, "\"v3 catalog search\" return wrong data by gamma criterion");
            }
        }

        switch (criterion) {
            case MyShopFilterPage.TOP_EM:
                for (ProductItemResponse eachProduct : productData) {
                    anAssert.isTrue(eachProduct.getTopEM(), "\"v3 catalog search\" return wrong data by topEm criterion");
                }
                break;
            case MyShopFilterPage.HAS_AVAILABLE_STOCK:
                for (ProductItemResponse eachProduct : productData) {
                    anAssert.isTrue(eachProduct.getAvailableStock() > 0, "\"v3 catalog search\" return wrong data by availableStock criterion");
                }
                break;
            case FilterPage.BEST_PRICE:
                for (ProductItemResponse eachProduct : productData) {
                    anAssert.isTrue(eachProduct.getPriceCategory().equals("BPR"), "\"v3 catalog search\" return wrong data by bestPrice criterion");
                }
                break;
            case FilterPage.CTM:
                for (ProductItemResponse eachProduct : productData) {
                    anAssert.isTrue(eachProduct.getCtm(), "\"v3 catalog search\" return wrong data by ctm criterion");
                }
                break;
            case FilterPage.TOP_1000:
                for (ProductItemResponse eachProduct : productData) {
                    anAssert.isTrue(eachProduct.getTop1000(), "\"v3 catalog search\" return wrong data by top1000 criterion");
                }
                break;
        }
        return this;
    }*/

}
