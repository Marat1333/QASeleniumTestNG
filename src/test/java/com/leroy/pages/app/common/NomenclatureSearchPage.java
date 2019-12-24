package com.leroy.pages.app.common;

import com.leroy.core.TestContext;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.pages.BasePage;
import com.leroy.core.web_elements.general.Element;
import io.qameta.allure.Step;

public class NomenclatureSearchPage extends BasePage {
    public NomenclatureSearchPage(TestContext context){
        super(context);
    }
    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup[following-sibling::android.widget.TextView]/android.view.ViewGroup")
    Element nomenclatureBackBtn;

    @AppFindBy(text = "ПОКАЗАТЬ ВСЕ ТОВАРЫ")
    Element showAllGoods;

    @Step("Перейти на окно выбора отдела")
    public void returnToDepartmentChoseWindow(){
        int counter=0;
        while (nomenclatureBackBtn.isVisible()){
            if (counter>3){
                break;
            }
            nomenclatureBackBtn.click();
            counter++;
        }
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
