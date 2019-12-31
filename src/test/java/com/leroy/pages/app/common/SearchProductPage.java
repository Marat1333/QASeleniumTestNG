package com.leroy.pages.app.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.pages.app.common.modal.SortModal;
import com.leroy.pages.app.sales.AddProductPage;
import com.leroy.pages.app.sales.SalesPage;
import com.leroy.pages.app.sales.widget.SearchProductCardWidget;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
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

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc='ScreenContent']//android.view.ViewGroup[android.widget.ImageView]",
            clazz = SearchProductCardWidget.class)
    private ElementList<SearchProductCardWidget> productCards;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"lmui-Icon\"]/ancestor::android.view.ViewGroup[2]")
    ElementList <Element> historyElementList;

    @AppFindBy(text = "Фильтр")
    Element filter;

    @AppFindBy(xpath = "(//android.view.ViewGroup[descendant::android.widget.TextView[@text='Фильтр']]/preceding-sibling::android.view.ViewGroup[descendant::android.widget.TextView])[2]//android.widget.TextView",
            metaName = "Выбранная номенклатура")
    Element nomenclature;

    @AppFindBy(xpath = "//android.view.ViewGroup[preceding-sibling::android.view.ViewGroup[2][ancestor::android.view.ViewGroup[@content-desc=\"ScreenContent\"]]]")
    Element sort;

    @AppFindBy(text = "Ты пока ничего не искал(а)")
    Element firstSearchMsg;

    Element discardAllFiltersBtn = E("contains(СБРОСИТЬ ФИЛЬТРЫ)");

    private final String SEARCH_HISTORY_ELEMENT = "//android.widget.TextView";
    private final String NOT_FOUND_MSG = "//*[contains(@text, 'Поиск «%s» не дал результатов')]";
    private List<String> visibleSearchHistory = new ArrayList<>();
    private int elementCounter=0;
    private final int searchHistoryMaxSize=20;

    @Override
    public void waitForPageIsLoaded() {
        searchField.waitForVisibility();
    }

    public List<String> getVisibleSearchHistory(){
        return visibleSearchHistory;
    }

    private void initializeVisibleSearchHistory()throws Exception{
        for (int i=0; i<historyElementList.getCount();i++){
            String tmp = historyElementList.get(i).findChildElement(SEARCH_HISTORY_ELEMENT).getText();
            if (!visibleSearchHistory.contains(tmp)) {
                visibleSearchHistory.add(tmp);
                elementCounter++;
            }
        }
    }

    // ---------------- Action Steps -------------------------//

    @Step("Перейти на главную страницу")
    public SalesPage backToSalesPage(){
        backBtn.click();
        return new SalesPage(context);
    }

    @Step("Ввести поисковой запрос {value} раз и инициировать поиск")
    public List<String> createSearchHistory(int value){
        List<String> searchHistory = new ArrayList<>();
        String tmp = "1";
        for (int i=0; i<value;i++) {
            enterTextInSearchFieldAndSubmit(tmp);
            searchHistory.add(tmp);
            tmp=tmp+"1";
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
    public SearchProductPage enterTextInSearchField(String text){
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

    public void verifySearchHistoryMaxSize(List<String> list)throws Exception{
        hideKeyboard();
        initializeVisibleSearchHistory();
        Element element=new Element(driver, new CustomLocator(By.xpath("//android.widget.TextView[@text='"+list.get(list.size()-searchHistoryMaxSize)+"']")));
        scrollDownTo(element);
        initializeVisibleSearchHistory();

        softAssert.isTrue(elementCounter==searchHistoryMaxSize,"История поиска состоит из 20 элементов");
        softAssert.isFalse(visibleSearchHistory.contains(list.get(list.size()-1-searchHistoryMaxSize)),"Не отображаются поисковые запросы, сделанные ранее последних 20 запросов");
        softAssert.verifyAll();
    }

    public void verifyElementsOfSearchHistoryContainsSearchPhrase(String searchPhrase)throws Exception{
        hideKeyboard();
        List<String> containsVisibleSearchHistory = new ArrayList<>();

        for (int i=0; i<historyElementList.getCount();i++){
            String tmp = historyElementList.get(i).findChildElement(SEARCH_HISTORY_ELEMENT).getText();
            if (!containsVisibleSearchHistory.contains(tmp)) {
                containsVisibleSearchHistory.add(tmp);
            }
        }

        for (String tmp : containsVisibleSearchHistory){
            System.out.println(tmp);
            anAssert.isTrue(tmp.contains(searchPhrase),"Каждое совпадение содержит поисковую строку");
        }
        anAssert.isTrue(containsVisibleSearchHistory.get(containsVisibleSearchHistory.size()-1).equals(searchPhrase),"Последний элемент истории поиска полностью совпадает с поисковой фразой");
    }

    public void shouldFirstSearchMsgBeDisplayed(){
        hideKeyboard();
        anAssert.isTrue(firstSearchMsg.isVisible(),"Отображено сообщение о первом поиске");
    }

    public void shouldNotFoundMsgBeDisplayed(String value){
        Element element = new Element(driver, By.xpath(String.format(NOT_FOUND_MSG,value)));
        anAssert.isTrue(element.isVisible(), "Поиск по запросу "+value+" не вернул результатов");
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
