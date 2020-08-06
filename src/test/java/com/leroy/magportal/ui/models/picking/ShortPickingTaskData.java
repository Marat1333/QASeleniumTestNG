package com.leroy.magportal.ui.models.picking;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.models.salesdoc.IDataWithNumberAndStatus;
import lombok.Data;

import java.util.List;

@Data
public class ShortPickingTaskData implements IDataWithNumberAndStatus<ShortPickingTaskData> {

    private String number;
    //private String orderLinkNumber;
    private PickingConst.AssemblyType assemblyType;
    private List<Integer> departments;
    private String status;
    private String client;
    private String collector;
    private String creationDate;
    private Double weight;
    private Double maxSize;

    @Override
    public void assertEqualsNotNullExpectedFields(ShortPickingTaskData expectedData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        if (expectedData.getNumber() != null) {
            softAssert.isEquals(number, expectedData.getNumber(),
                    "Неверный номер документа");
        }
        if (expectedData.getAssemblyType() != null) {
            softAssert.isEquals(assemblyType, expectedData.getAssemblyType(),
                    "Неверный тип сборки документа");
        }
        if (expectedData.getDepartments() != null) {
            softAssert.isEquals(departments, expectedData.getDepartments(),
                    "Неверный отделы у документа");
        }
        if (expectedData.getStatus() != null) {
            softAssert.isEquals(status.toLowerCase(), expectedData.getStatus().toLowerCase(),
                    "Неверный статус у документа");
        }
        if (expectedData.getClient() != null) {
            softAssert.isEquals(client, expectedData.getClient(),
                    "Неверный клиент у документа");
        }
        if (expectedData.getCollector() != null) {
            softAssert.isEquals(collector, expectedData.getCollector(),
                    "Неверный сборщик у документа");
        }
        if (expectedData.getCreationDate() != null) {
            softAssert.isEquals(creationDate, expectedData.getCreationDate(),
                    "Неверная дата создания документа");
        }
        if (expectedData.getWeight() != null) {
            softAssert.isTrue(Math.abs(this.getWeight() - expectedData.getWeight()) <= 0.011,
                    "Неверный вес товаров в документе");
        }
        if (expectedData.getMaxSize() != null) {
            softAssert.isEquals(maxSize, expectedData.getMaxSize(),
                    "Неверный максимальный размер в документе");
        }
        softAssert.verifyAll();
    }
}
