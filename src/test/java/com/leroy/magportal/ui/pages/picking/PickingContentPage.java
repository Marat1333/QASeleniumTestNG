package com.leroy.magportal.ui.pages.picking;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magportal.ui.models.picking.PickingProductCardData;
import com.leroy.magportal.ui.pages.picking.widget.BuildProductCardWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import io.qameta.allure.Step;

public class PickingContentPage extends PickingPage {

    @WebFindBy(xpath = "//div[substring(@class, string-length(@class) - string-length('order-ProductCard') +1) = 'order-ProductCard']")
    CardWebWidgetList<BuildProductCardWidget, PickingProductCardData> productCards;

    // Bottom area

    @WebFindBy(xpath = "//div[contains(@class, 'Picking-InfoCard')]//button", metaName = "Кнопка 'Начать сборку'")
    Button startAssemblyBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'Picking-InfoCard')]//button[2]", metaName = "Кнопка 'Редактировать сборку'")
    Button editAssemblyBtn;

    @WebFindBy(xpath = "//button[descendant::span[text()='РАЗДЕЛИТЬ']]", metaName = "Кнопка 'Разделить' в нижней области")
    Button splitAssemblyBtn;

    // Actions

    @Step("Нажать кнопку редактирования сборк")
    public PickingContentPage clickEditAssemblyButton() {
        editAssemblyBtn.click();
        waitForSpinnerAppearAndDisappear();
        return this;
    }

    @Step("Нажать кнопку 'Разделить' для {index}-ого товара")
    public PickingContentPage setSplitForProductCard(int index, boolean val) throws Exception {
        productCards.get(index).setSplitOption(val);
        return this;
    }

    @Step("Нажать кнопку 'Разделить' на нижней панели")
    public PickingContentPage clickSplitAssemblyButton() {
        splitAssemblyBtn.click();
        return this;
    }

}
