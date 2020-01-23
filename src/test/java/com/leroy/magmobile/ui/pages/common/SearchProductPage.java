package com.leroy.magmobile.ui.pages.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.ui.pages.common.modal.SortPage;
import com.leroy.magmobile.ui.pages.sales.AddProductPage;
import com.leroy.magmobile.ui.pages.sales.SalesPage;
import com.leroy.magmobile.ui.pages.sales.product_card.ProductDescriptionPage;
import com.leroy.magmobile.ui.pages.sales.widget.SearchProductCardWidget;
import com.leroy.models.ProductCardData;
import com.leroy.models.TextViewData;
import io.qameta.allure.Step;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.magmobile.data.ProductItemListResponse;
import ru.leroymerlin.qa.core.clients.magmobile.data.ProductItemResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SearchProductPage extends BaseAppPage {

    public SearchProductPage(TestContext context) {
        super(context);
    }

    @AppFindBy(accessibilityId = "back", metaName = "Кнопка назад")
    private Element backBtn;

    @AppFindBy(accessibilityId = "Button", metaName = "Кнопка для сканирования штрихкода")
    private Element scanBarcodeBtn;

    @AppFindBy(accessibilityId = "ScreenTitle-CatalogComplexSearchStore", metaName = "Поле ввода текста для поиска")
    private EditBox searchField;

    private AndroidScrollView<TextViewData> searchHistoryScrollView = new AndroidScrollView<>(driver,
            new CustomLocator(By.xpath("//android.widget.ScrollView"), null,
                    "Виджет прокрутки для истории последних запросов", false));

    private AndroidScrollView<ProductCardData> productCardsScrollView = new AndroidScrollView<>(driver,
            new CustomLocator(By.xpath("//android.view.ViewGroup[@content-desc=\"ScreenContent\"]/android.view.ViewGroup[1]/android.view.ViewGroup/android.widget.ScrollView"), null,
                    "", false),
            ".//android.view.ViewGroup[android.widget.ImageView]", SearchProductCardWidget.class);


    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']//android.view.ViewGroup[android.widget.ImageView]",
            clazz = SearchProductCardWidget.class)
    private ElementList<SearchProductCardWidget> productCards;

    @AppFindBy(text = "Фильтр")
    Element filter;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']/android.view.ViewGroup[2]/android.view.ViewGroup[1]//android.widget.TextView",
            metaName = "Выбранная номенклатура")
    Element nomenclature;

    @AppFindBy(xpath = "\t//android.view.ViewGroup[@content-desc=\"ScreenContent\"]/android.view.ViewGroup[2]/android.view.ViewGroup[3]")
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

    private void scrollNTimesAndAddVisibleElementsToArray(int i, ArrayList<Integer> arrayList, String sortType) throws Exception {
        for (int y = 0; y < i; y++) {
            addNeededDataToArrayFromProductCard(arrayList, sortType);
            scrollDown();
            waitForProgressBarIsInvisible();
        }
    }

    private void addNeededDataToArrayFromProductCard(ArrayList<Integer> arrayList, String sortType) throws Exception {
        String content = "";
        int contentNumber = 0;

        for (Element tmp : productCards) {
            if (sortType.equals(SortPage.SORT_BY_LM_ASC) || sortType.equals(SortPage.SORT_BY_LM_DESC)) {
                content = tmp.findChildElement("//android.widget.TextView[1]").getText();
                content = content.replaceAll("\\D", "");
                if (content.length() > 0) {
                    contentNumber = Integer.valueOf(content);
                }
                if (!arrayList.contains(contentNumber) && (content.length() == 8)) {
                    arrayList.add(contentNumber);
                }
            } else if (sortType.equals(SortPage.SORT_BY_AVAILABLE_STOCK_ASC) || sortType.equals(SortPage.SORT_BY_AVAILABLE_STOCK_DESC)) {
                try {
                    content = tmp.findChildElement("//android.widget.TextView[6]").getText();
                    if (!arrayList.contains(tmp) && content.length() > 0) {
                        content = content.replaceAll(" ", "");
                        if (content.contains("-") || content.matches("\\d+")) {
                            contentNumber = Integer.valueOf(content);
                            arrayList.add(contentNumber);
                        }
                    }
                } catch (NoSuchElementException e) {
                    log.assertFail("Для карточки товара не найден остаток");
                }
            }
        }
    }

    private ArrayList<Integer> getSortedElementsFromProductCards(String sortType, int scrollNTimes) throws Exception {
        ArrayList<Integer> sortedCodes = new ArrayList<>();
        ArrayList<Integer> sortedStocks = new ArrayList<>();

        if (sortType.equals(SortPage.SORT_BY_AVAILABLE_STOCK_ASC) || sortType.equals(SortPage.SORT_BY_AVAILABLE_STOCK_DESC)) {
            scrollNTimesAndAddVisibleElementsToArray(scrollNTimes, sortedStocks, sortType);
            return sortedStocks;
        } else if (sortType.equals(SortPage.SORT_BY_LM_ASC) || sortType.equals(SortPage.SORT_BY_LM_DESC)) {
            scrollNTimesAndAddVisibleElementsToArray(scrollNTimes, sortedCodes, sortType);
            return sortedCodes;
        } else {
            return new ArrayList<>();
        }
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
        //TODO Добавить проверку на наличие ошибки ответа сервака
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
    public MyShopFilterPage goToFilterPage() {
        filter.click();
        return new MyShopFilterPage(context);
    }

    @Step("Открыть окно сортировки")
    public SortPage openSortPage() {
        sort.click();
        return new SortPage(context);
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
                            String.format("Товар с кодом %s не содержит текст %s", card.getLmCode(false), text));
                }
            } else {
                anAssert.isTrue(card.getBarCode(true).contains(text) ||
                                card.getName().contains(text) || card.getLmCode(false).contains(text),
                        String.format("Товар с кодом %s не содержит текст %s", card.getLmCode(false), text));
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
        anAssert.isFalse(productCards.get(index).getBarCode(true).isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустой штрихкод", index));
        anAssert.isFalse(productCards.get(index).getLmCode(true).isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустой номер", index));
        anAssert.isFalse(productCards.get(index).getName().isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустое название", index));
        anAssert.isFalse(productCards.get(index).getPrice().isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустую цену", index));
        anAssert.isEquals(productCards.get(index).getPriceLbl(), "за штуку",
                String.format("Карточка под индексом %s должна иметь примечание 'за штуку'", index));
        anAssert.isFalse(productCards.get(index).getQuantity(true).isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустое кол-во", index));
        anAssert.isEquals(productCards.get(index).getQuantityLbl(), "доступно",
                String.format("Карточка под индексом %s должна иметь примечание 'доступно'", index));
        anAssert.isFalse(productCards.get(index).getQuantityType().isEmpty(),
                String.format("Карточка под индексом %s не должна иметь пустой тип кол-ва", index));
        return this;
    }

    // API verifications

    //TODO Переопределить equals для ProductCardData

    public SearchProductPage shouldResponceEqualsContent(Response<ProductItemListResponse> response, boolean strictEquality) throws Exception{
        List<ProductItemResponse> productData = response.asJson().getItems();
        List<ProductCardData> productCardData = productCardsScrollView.getFullDataList(10);
        if (productCardData.size()!=productData.size()){
            throw new Exception("Page size param should be equals to maxEntityCount");
        }
        if (strictEquality) {
            for (int i = 0; i < productCardData.size(); i++) {
                anAssert.isEquals(productCardData.get(i).getLmCode(), productData.get(i).getLmCode(), "ЛМ коды не совпадают");
            }
        }else {
            for (int i=0;i<productCardData.size();i++){
                anAssert.isTrue(productCardData.get(i).getLmCode().contains(productData.get(i).getLmCode()),"Продукты частично не совпадают по LM "+productData.get(i).getLmCode());
                anAssert.isTrue(productCardData.get(i).getBarCode().contains(productData.get(i).getBarCode()),"Продукты частично совпадают по BAR "+productData.get(i).getBarCode());
                anAssert.isTrue(productCardData.get(i).getBarCode().contains(productData.get(i).getBarCode()),"Продукты частично совпадают по BAR "+productData.get(i).getBarCode());
            }
        }
        return this;
    }

    @Step("Проверить, что карточки с товарами отсортированы {sortType}")
    public SearchProductPage shouldProductCardsBeSorted(String sortType, int howManyProductsNeedToVerify) throws Exception {
        List<ProductCardData> dataList = productCardsScrollView.getFullDataList(howManyProductsNeedToVerify);
        anAssert.isFalse(dataList.size() == 0, "На странице отсутствуют карточки товаров");
        List<ProductCardData> expectedSortedList = new ArrayList<>(dataList);
        switch (sortType) {
            case SortPage.SORT_BY_AVAILABLE_STOCK_ASC:
                expectedSortedList.sort((d1, d2) ->
                        Integer.parseInt(d2.getAvailableQuantity()) - Integer.parseInt(d1.getAvailableQuantity()));
                break;
            case SortPage.SORT_BY_AVAILABLE_STOCK_DESC:
                expectedSortedList.sort(Comparator.comparingInt(d -> Integer.parseInt(d.getAvailableQuantity())));
                break;
            case SortPage.SORT_BY_LM_ASC:
                expectedSortedList.sort(Comparator.comparingInt(d -> Integer.parseInt(d.getLmCode())));
                break;
            case SortPage.SORT_BY_LM_DESC:
                expectedSortedList.sort((d1, d2) ->
                        Integer.parseInt(d2.getLmCode()) - Integer.parseInt(d1.getLmCode()));
                break;
            default:
                anAssert.isTrue(false,
                        "Не предусмотрен метод для проверки сортировки с типом: " + sortType);
                break;
        }
        anAssert.isEquals(dataList, expectedSortedList, "Элементы отсортированы некорректно");
        return this;
    }


}
