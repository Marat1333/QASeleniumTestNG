package com.leroy.magportal.ui.models.picking;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.pages.picking.modal.SplitPickingModalStep1;
import com.leroy.utils.ParserUtil;
import lombok.Data;

import java.util.ArrayList;
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

    public String getOrderLinkNumber() {
        return number.split(" ")[1].replaceAll("\\*", "");
    }

    public List<SplitPickingModalStep1.SplitProductCardData> getSplitPickingProductDataList() {
        List<SplitPickingModalStep1.SplitProductCardData> splitProductCardDataList = new ArrayList<>();
        for (PickingProductCardData productCardData : products) {
            SplitPickingModalStep1.SplitProductCardData splitProduct = new SplitPickingModalStep1.SplitProductCardData(productCardData);
            splitProductCardDataList.add(splitProduct);
        }
        return splitProductCardDataList;
    }

    public PickingTaskData clone() {
        PickingTaskData pickingTaskData = new PickingTaskData();
        pickingTaskData.setNumber(number);
        pickingTaskData.setAssemblyType(assemblyType);
        pickingTaskData.setStatus(status);
        pickingTaskData.setCreationDate(creationDate);
        List<PickingProductCardData> cloneProducts = new ArrayList<>();
        for (PickingProductCardData thisProduct : products) {
            cloneProducts.add(thisProduct.clone());
        }
        pickingTaskData.setProducts(cloneProducts);
        return pickingTaskData;
    }

    public ShortPickingTaskData getShortData() {
        ShortPickingTaskData shortPickingTaskData = new ShortPickingTaskData();
        shortPickingTaskData.setNumber(number);
        shortPickingTaskData.setAssemblyType(assemblyType);
        shortPickingTaskData.setStatus(status);
        shortPickingTaskData.setCreationDate(creationDate);
        double thisWeight = 0.0;
        for (PickingProductCardData productData : products) {
            thisWeight += productData.getWeight() * productData.getOrderedQuantity();
        }
        shortPickingTaskData.setWeight(thisWeight);
        String[] dimension = products.get(0).getDimension().split(" ");
        List<Double> dimensionDouble = new ArrayList<>();
        for (String size : dimension) {
            size = size.replaceAll("[^\\d+\\,\\-]", "");
            if (!size.isEmpty())
                dimensionDouble.add(ParserUtil.strToDouble(size));
        }
        shortPickingTaskData.setMaxSize(dimensionDouble.stream().max(Double::compare).get());
        shortPickingTaskData.setDepartments(Arrays.asList(products.get(0).getDepartment()));
        return shortPickingTaskData;
    }

    public void assertEqualsNotNullExpectedFields(PickingTaskData expectedTaskData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        softAssert.isEquals(number, expectedTaskData.getNumber(), "Неверный номер сборки");
        softAssert.isEquals(assemblyType, expectedTaskData.getAssemblyType(), "Неверный тип сборки");
        softAssert.isEquals(status.toLowerCase(), expectedTaskData.getStatus().toLowerCase(), "Неверный статус");
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
