package com.leroy.pages.app.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BaseAppPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import io.qameta.allure.Step;
import org.openqa.selenium.NoSuchElementException;

public class NomenclatureSearch extends BaseAppPage {
    public NomenclatureSearch(TestContext context){
        super(context);
    }
    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[following-sibling::android.widget.TextView]/android.view.ViewGroup", metaName = "Вернуться на элемент назад")
    Element nomenclatureBackBtn;

    @AppFindBy(text = "ПОКАЗАТЬ ВСЕ ТОВАРЫ", metaName = "Кнопка показать товары")
    Element showAllGoods;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[preceding-sibling::android.view.ViewGroup[1]]", metaName = "Окно выбора department")
    ElementList<Element> firstLevelNomenclatureElementsList;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[preceding-sibling::android.view.ViewGroup[2]]", metaName = "Окно выбора последующих элементов")
    ElementList<Element> secondLevelNomenclatureElementsList;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[preceding-sibling::android.view.ViewGroup]")
    ElementList<Element> thirdLevelNomenclatureElementsList;

    private final String eachElementOfNomenclatureXpath = "./android.view.ViewGroup/android.widget.TextView";


    @Step("Перейти на окно выбора отдела")
    public void returnToDepartmentChoseWindow(){
        while (nomenclatureBackBtn.isVisible()){
            nomenclatureBackBtn.click();
        }
    }

/**
 Перемещение на любой уровень номенклатуры

 selectElementFromArray - выбрать элемент на странице

 firstLevelNomenclatureElementsList - первая страница с departmentId

 secondLevelNomenclatureElementsList - последующие страницы

*/

    @Step("Выбрать отдел {dept}, подотдел {subDept}, раздел {classId}, подраздел {subClass}")
    public void choseDepartmentId(Integer dept, Integer subDept, Integer classId, Integer subClass) throws Exception{
        returnToDepartmentChoseWindow();
        try {
            if (dept != null) {
                selectElementFromArray(dept, firstLevelNomenclatureElementsList);
            }
            if (dept != null && subDept != null) {
                selectElementFromArray(subDept, secondLevelNomenclatureElementsList);
            }
            if (dept != null && subDept != null && classId != null) {
                selectElementFromArray(classId, secondLevelNomenclatureElementsList);
            }
            if (dept != null && subDept != null && classId != null && subClass != null) {
                selectElementFromArray(subClass, secondLevelNomenclatureElementsList);
            }
        }
        catch (NoSuchElementException e)
        {
            e.printStackTrace();
        }
    }

    private void selectElementFromArray(Integer value, ElementList<Element> someArray)throws Exception{
        int counter=0;
        for (Element element : someArray){
            String tmp = element.findChildElement(eachElementOfNomenclatureXpath).getText().replaceAll("^0+","");
            tmp = tmp.replaceAll("\\D+", "");
            if (tmp.length()>4){
                tmp=tmp.substring(0,4);
            }
            if (tmp.equals(String.valueOf(value))){
                element.findChildElement(eachElementOfNomenclatureXpath).click();
                counter++;
                break;
            }
        }
        if (counter<1){
            scrollDown();
            selectElementFromArray(value, thirdLevelNomenclatureElementsList);
        }
        counter=0;
    }


    public SearchProductPage viewAllProducts(){
        showAllGoods.click();
        return new SearchProductPage(context);
    }

    @Override
    public void waitForPageIsLoaded() {
        showAllGoods.waitForVisibility();
    }
}
