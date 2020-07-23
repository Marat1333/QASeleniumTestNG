package com.leroy.magportal.ui.models.picking;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.utils.ParserUtil;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class PickingTaskData {
    private String number;
    //private String orderLinkNumber;
    private PickingConst.AssemblyType assemblyType;
    private String status;
    private String creationDate;
    private List<PickingProductCardData> products;

    public ShortPickingTaskData getShortData() {
        ShortPickingTaskData shortPickingTaskData = new ShortPickingTaskData();
        shortPickingTaskData.setNumber(number);
        shortPickingTaskData.setAssemblyType(assemblyType);
        shortPickingTaskData.setStatus(status);
        shortPickingTaskData.setCreationDate(creationDate);
        shortPickingTaskData.setWeight(products.get(0).getWeight());
        shortPickingTaskData.setMaxSize(ParserUtil.strToDouble(products.get(0).getDimension().split(" ")[0]));
        shortPickingTaskData.setDepartments(Arrays.asList(products.get(0).getDepartment()));
        return shortPickingTaskData;
    }

    public void assertEqualsNotNullExpectedFields(PickingTaskData expectedTaskData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        softAssert.isEquals(number, expectedTaskData.getNumber(), "Неверный номер сборки");
        softAssert.isEquals(assemblyType, expectedTaskData.getAssemblyType(), "Неверный тип сборки");
        softAssert.isEquals(status, expectedTaskData.getStatus(), "Неверный статус");
        if (expectedTaskData.getCreationDate() != null)
            softAssert.isEquals(creationDate, expectedTaskData.getCreationDate(), "Неверная дата создания сборки");
        softAssert.isEquals(products.size(), expectedTaskData.getProducts().size(), "Неверное кол-во товаров");
        softAssert.verifyAll();
        for (int i = 0; i < expectedTaskData.getProducts().size(); i++) {
            PickingProductCardData actualProduct = products.get(i);
            PickingProductCardData expectedProduct = expectedTaskData.getProducts().get(i);
            actualProduct.assertEqualsNotNullExpectedFields(i, expectedProduct);
        }

    }
}
