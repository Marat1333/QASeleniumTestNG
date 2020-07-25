package com.leroy.magportal.ui.pages.picking;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magportal.ui.models.picking.PickingProductCardData;
import com.leroy.magportal.ui.models.picking.PickingTaskData;
import com.leroy.magportal.ui.pages.picking.modal.SplitPickingModalStep1;
import com.leroy.magportal.ui.pages.picking.widget.AssemblyProductCardWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.magportal.ui.webelements.commonelements.PuzCheckBox;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.List;

public class PickingContentPage extends PickingPage {

    @WebFindBy(xpath = "//button[contains(@class, 'PickingView__tab__checkbox')]", metaName = "Опция 'Выбрать все'")
    PuzCheckBox selectAllChkBox;

    @WebFindBy(xpath = "//div[substring(@class, string-length(@class) - string-length('order-ProductCard') +1) = 'order-ProductCard']",
            clazz = AssemblyProductCardWidget.class)
    CardWebWidgetList<AssemblyProductCardWidget, PickingProductCardData> productCards;

    // Bottom area

    @WebFindBy(xpath = "//div[contains(@class, 'Picking-InfoCard')]//button", metaName = "Кнопка 'Начать сборку'")
    Button startAssemblyBtn;

    @WebFindBy(xpath = "//div[contains(@class, 'Picking-InfoCard')]//div[contains(@class, 'popover')]//button",
            metaName = "Кнопка 'Редактировать сборку'")
    Button editAssemblyBtn;

    @WebFindBy(xpath = "//button[descendant::span[text()='РАЗДЕЛИТЬ']]", metaName = "Кнопка 'Разделить' в нижней области")
    Button splitAssemblyBtn;

    @Override
    public void waitForPageIsLoaded() {
        productCards.waitUntilAtLeastOneElementIsPresent();
    }

    // Grab information

    @Step("Забрать информацию о Сборке")
    public PickingTaskData getPickingTaskData() throws Exception {
        PickingTaskData pickingTaskData = new PickingTaskData();
        pickingTaskData.setNumber(getNumber());
        pickingTaskData.setAssemblyType(getAssemblyType());
        pickingTaskData.setStatus(getStatus());
        pickingTaskData.setCreationDate(getCreationDate());
        boolean onlySelectedProducts = false;
        if (!onlySelectedProducts)
            pickingTaskData.setProducts(productCards.getDataList());
        else {
            List<PickingProductCardData> productCardDataList = new ArrayList<>();
            for (AssemblyProductCardWidget widget : productCards) {
                if (widget.isSplitChecked())
                    productCardDataList.add(widget.collectDataFromPage());
            }
            pickingTaskData.setProducts(productCardDataList);
        }
        return pickingTaskData;
    }

    // Actions

    @Step("Нажать кнопку редактирования сборк")
    public PickingContentPage clickEditAssemblyButton() {
        editAssemblyBtn.click();
        waitForSpinnerAppearAndDisappear();
        anAssert.isElementVisible(selectAllChkBox);
        anAssert.isElementVisible(splitAssemblyBtn);
        return this;
    }

    @Step("Нажать кнопку 'Разделить' для {index}-ого товара")
    public PickingContentPage setSplitForProductCard(int index, boolean val) throws Exception {
        index--;
        productCards.get(index).setSplitOption(val);
        return this;
    }

    @Step("Нажать кнопку 'Разделить' на нижней панели")
    public SplitPickingModalStep1 clickSplitAssemblyButton() {
        splitAssemblyBtn.click();
        return new SplitPickingModalStep1();
    }

    // Verifications

    @Step("Проверить выбрана ли опция 'Выбрать все'")
    public PickingContentPage shouldSelectAllOptionIsSelected() throws Exception {
        anAssert.isTrue(selectAllChkBox.isChecked(), "Опция 'Выбрать все' должна быть выбрана");
        return this;
    }

    @Step("Проверить, что данные сборки отображаются корректно")
    public PickingContentPage shouldPickingTaskDataIs(PickingTaskData expectedPickingTaskData) throws Exception {
        PickingTaskData actualData = getPickingTaskData();
        actualData.assertEqualsNotNullExpectedFields(expectedPickingTaskData);
        return this;
    }

}
