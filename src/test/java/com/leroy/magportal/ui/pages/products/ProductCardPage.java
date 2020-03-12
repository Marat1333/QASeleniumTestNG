package com.leroy.magportal.ui.pages.products;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.configuration.Log;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magportal.ui.pages.common.MenuPage;
import io.qameta.allure.Step;

import java.util.NoSuchElementException;

public class ProductCardPage extends MenuPage {
    public ProductCardPage(TestContext context) {
        super(context);
    }

    @WebFindBy(xpath = "//span[contains(text(), 'К результатам поиска')]")
    Button backToSearchResults;

    @WebFindBy(xpath = "//span[contains(text(), 'Каталог товаров')]/ancestor::div[2]/div", clazz = Button.class)
    ElementList<Button> nomenclaturePath;

    @WebFindBy(xpath = "//span[contains(@class, 'LmCode')]/following-sibling::span")
    Element lmCodeLbl;

    @WebFindBy(xpath = "//div[@id='barCodeButton']/div[1]/span")
    Element barCodeLbl;

    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][1]")
    Element nomenclatureBadge;

    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][2]")
    private Element gammaBadge;

    @WebFindBy(xpath = "//span[contains(@class, 'Badge')][3]")
    private Element categoryBadge;

    @WebFindBy(xpath = "//p[contains(text(), 'Характеристики')]/ancestor::div[3]/preceding-sibling::span")
    Element productTitle;

    @WebFindBy(xpath = "//span[contains(text(),'ВСЕ ХАРАКТЕРИСТИКИ')]")
    Element showAllSpecifications;

    @WebFindBy(xpath = "//span[contains(text(),'ВСЕ ОПИСАНИЕ')]")
    Element showFullDescription;

    @Step("Вернуться к результатам поиска")
    public SearchProductPage backToSearchResult() {
        backToSearchResults.click();
        return new SearchProductPage(context);
    }

    @Step("Перейти на страницу с результатами поиска по элементу номенклатуры {nomenclatureElementName}")
    public SearchProductPage backToNomenclatureElement(String nomenclatureElementName) throws Exception {
        Button targetElement=null;
        for (Button each : nomenclaturePath) {
            try {
                targetElement = (Button) each.findChildElement("/descendant::span[2][contains(text(),'" + nomenclatureElementName + "')]/ancestor::div[1]");
                if (targetElement.isPresent()){
                    break;
                }
            }catch (NoSuchElementException e){
                Log.warn("Try to find element");
            }
        }
        targetElement.click();
        return new SearchProductPage(context);
    }

    @Step("Открыть полное описание товара")
    public ProductCardPage showFullDescription(){
        showFullDescription.click();
        return this;
    }

    @Step("Показать все характеристики товара")
    public ProductCardPage showAllSpecifications(){
        showAllSpecifications.click();
        return this;
    }

    

}
