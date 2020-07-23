package com.leroy.magportal.ui.pages.picking.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.models.picking.ShortPickingTaskData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

import java.util.stream.Collectors;

public class ShortPickingTaskCardWidget extends CardWebWidget<ShortPickingTaskData> {

    public ShortPickingTaskCardWidget(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    private final static String HEADER_XPATH = ".//div[contains(@class, 'Picking-PickingListItem__header')]";
    @WebFindBy(xpath = HEADER_XPATH + "/div[1]/span[1]")
    Element number;

    @WebFindBy(xpath = HEADER_XPATH + "/div[1]/span[2]")
    Element assemblyType;

    @WebFindBy(xpath = HEADER_XPATH + "/div[2]/span[1]")
    Element maxSize;

    @WebFindBy(xpath = HEADER_XPATH + "/div[2]/span[3]")
    Element weight;

    @WebFindBy(xpath = ".//span[contains(@class, 'Status-container')]")
    Element status;

    @WebFindBy(xpath = ".//div[span[contains(text(), 'Сборщик') or contains(text(), 'Клиент')]]")
    Element collectorOrClientLbl;

    @WebFindBy(xpath = ".//div[contains(@class, 'PickingListItem__time')]/div/div/span[3]")
    Element creationDate;

    @WebFindBy(xpath = ".//div[contains(@class, 'PickingListItem__departments-item')]")
    ElementList<Element> departments;

    private PickingConst.AssemblyType getAssemblyType() {
        String assemblyTypeText = this.assemblyType.getText();
        switch (assemblyTypeText) {
            case "торг.зал":
                return PickingConst.AssemblyType.SHOPPING_ROOM;
        }
        return null;
    }

    @Override
    public ShortPickingTaskData collectDataFromPage() throws Exception {
        ShortPickingTaskData pickingTaskData = new ShortPickingTaskData();
        pickingTaskData.setNumber(number.getText());
        pickingTaskData.setAssemblyType(getAssemblyType());
        pickingTaskData.setMaxSize(ParserUtil.strToDouble(maxSize.getText(), "."));
        pickingTaskData.setWeight(ParserUtil.strToDouble(weight.getText(), "."));
        pickingTaskData.setStatus(status.getText());
        pickingTaskData.setDepartments(departments.getTextList().stream()
                .map(Integer::valueOf).collect(Collectors.toList()));
        pickingTaskData.setCreationDate(creationDate.getText());
        String collectorOrClient = collectorOrClientLbl.getText();
        if (collectorOrClient.contains("Сборщик:"))
            pickingTaskData.setCollector(collectorOrClient.replaceAll("Сборщик:", "").trim());
        else
            pickingTaskData.setClient(collectorOrClient.replaceAll("Клиент:", "").trim());
        return pickingTaskData;
    }
}
