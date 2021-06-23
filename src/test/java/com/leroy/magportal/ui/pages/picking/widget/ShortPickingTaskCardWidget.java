package com.leroy.magportal.ui.pages.picking.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.models.picking.ShortPickingTaskData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.utils.ParserUtil;
import lombok.SneakyThrows;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ShortPickingTaskCardWidget extends CardWebWidget<ShortPickingTaskData> {

    public ShortPickingTaskCardWidget(WebDriver driver, CustomLocator customLocator) {
        super(driver, customLocator);
    }

    private final static String HEADER_XPATH = ".//div[contains(@class, 'lmui-View-between')]";
    @WebFindBy(xpath = HEADER_XPATH + "[2]/div[1]/div[1]//span")
    ElementList<Element> number;

    @WebFindBy(xpath = HEADER_XPATH + "[2]/div[1]/div[2]//span[1]")
    Element assemblyType;

    @WebFindBy(xpath = HEADER_XPATH + "[2]/div[2]/div/div[1]//span[1]")
    Element maxSize;

    @WebFindBy(xpath = HEADER_XPATH + "[2]/div[2]/div/div[2]//span[1]")
    Element weight;

    @WebFindBy(xpath = ".//span[contains(@class, 'Status-container')]")
    Element status;

    @WebFindBy(xpath = ".//div[span[contains(text(), 'Сборщик') or contains(text(), 'Клиент')]]")
    Element collectorOrClientLbl;

    @WebFindBy(xpath = ".//div[contains(@class, 'PickingListItem__time')]/div/div/span[3]")
    Element creationDate;

    @WebFindBy(xpath = ".//div[contains(@class, 'picking-DepartmentLabel__department')]")
    ElementList<Element> departments;

    private PickingConst.AssemblyType getAssemblyType() {
        String assemblyTypeText = this.assemblyType.getText().toLowerCase();
        switch (assemblyTypeText) {
            case "торг.зал":
                return PickingConst.AssemblyType.SHOPPING_ROOM;
            case "склад":
                return PickingConst.AssemblyType.STOCK;
            case ">":
                return PickingConst.AssemblyType.SS;
        }
        return null;
    }

    @Override
    public ShortPickingTaskData collectDataFromPage() throws Exception {
        ShortPickingTaskData pickingTaskData = new ShortPickingTaskData();
        pickingTaskData.setNumber(getNumber());
        pickingTaskData.setAssemblyType(getAssemblyType());
        pickingTaskData.setMaxSize(ParserUtil.strToDouble(maxSize.getText(), "."));
        pickingTaskData.setWeight(ParserUtil.strToDouble(weight.getText(), "."));
        pickingTaskData.setStatus(status.getText());
        if (departments.getCount() > 0) {
            pickingTaskData.setDepartments(departments.getTextList().stream()
                    .map(Integer::valueOf).collect(Collectors.toList()));
        } else {
            pickingTaskData.setDepartments(new ArrayList<>());
        }
        pickingTaskData.setCreationDate(creationDate.getTextIfPresent());
        String collectorOrClient = collectorOrClientLbl.getTextIfPresent();
        if (collectorOrClient != null) {
            if (collectorOrClient.contains("Сборщик:"))
                pickingTaskData.setCollector(collectorOrClient.replaceAll("Сборщик:", "").trim());
            else
                pickingTaskData.setClient(collectorOrClient.replaceAll("Клиент:", "").trim());
        }
        return pickingTaskData;
    }

    private String getNumber() throws Exception {
        return String.join(" ", number.getTextList());
    }
}
