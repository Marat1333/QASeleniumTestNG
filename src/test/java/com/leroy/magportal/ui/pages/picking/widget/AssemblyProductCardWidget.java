package com.leroy.magportal.ui.pages.picking.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.picking.PickingProductCardData;
import com.leroy.magportal.ui.webelements.CardWebWidget;
import com.leroy.magportal.ui.webelements.commonelements.PuzCheckBox;
import com.leroy.utils.ParserUtil;
import org.openqa.selenium.WebDriver;

public class AssemblyProductCardWidget extends CardWebWidget<PickingProductCardData> {

    public AssemblyProductCardWidget(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    @WebFindBy(xpath = ".//button[contains(@class, 'Picking-ProductCard__checkbox')]",
            metaName = "Чекбокс разделить")
    PuzCheckBox splitChkBox;

    @WebFindBy(xpath = ".//div[contains(@class, 'shared-order-StyledLabel')]", metaName = "Номер отдела")
    Element department;

    @WebFindBy(xpath = ".//div[span[contains(@class, 'LmCode__accent')]]", metaName = "ЛМ код")
    Element lmCode;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCard__barcodes')]", metaName = "Бар код")
    Element barCode;

    @WebFindBy(xpath = ".//p[contains(@class, 'ProductCard__body-title')]", metaName = "Название товара")
    Element title;

    @WebFindBy(xpath = ".//span[contains(@class, 'Price-container')]", metaName = "Цена")
    Element price;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCardFooter__smallSide')]//p[2]", metaName = "Вес")
    Element weight;

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCardFooter__bigSide')]//p[2]", metaName = "Габариты")
    Element dimension;

    @WebFindBy(xpath = ".//div[contains(@id, 'StockInformationExpander')]/span[2]", metaName = "Кол-во на складе")
    Element stockQuantity;

    public String getStockQuantity() {
        return stockQuantity.getText();
    }

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCard__quantities')]/div[2]//input",
            metaName = "Поле 'Заказано'")
    EditBox orderedQuantityFld;

    public String getOrderedQuantity() {
        return orderedQuantityFld.getText();
    }

    @WebFindBy(xpath = ".//div[contains(@class, 'ProductCard__quantities')]/div[3]//input",
            metaName = "Поле 'Собрано'")
    EditBox collectedQuantityFld;

    public String getCollectedQuantity() {
        return collectedQuantityFld.getText();
    }

    @WebFindBy(xpath = ".//div[contains(@class, 'Picking-ProductCard__reason')]"
            , metaName = "Причина отсутствия")
    Element productCardReason;


    // Actions

    public void editCollectQuantity(String val) {
        collectedQuantityFld.clear(true);
        collectedQuantityFld.fill(val);
        collectedQuantityFld.sendBlurEvent();
    }

    public void editCollectQuantity(int val) {
        editCollectQuantity(String.valueOf(val));
    }

    public void setSplitOption(boolean val) throws Exception {
        splitChkBox.setValue(val);
    }

    public void clickReason() {
        productCardReason.scrollTo();
        productCardReason.click(short_timeout);
    }

    // Check or Get data
    public boolean isSplitChecked() throws Exception {
        return splitChkBox.isChecked();
    }

    public String getReason() {
        return productCardReason.getTextIfPresent();
    }

    @Override
    public PickingProductCardData collectDataFromPage() {
        PickingProductCardData pickingProductCardData = new PickingProductCardData();
        pickingProductCardData.setBarCode(barCode.getText().replaceAll(">|<|\n| ", "").trim());
        pickingProductCardData.setLmCode(lmCode.getText());
        pickingProductCardData.setDepartment(ParserUtil.strToInt(department.getText()));
        pickingProductCardData.setDimension(dimension.getText());
        pickingProductCardData.setTitle(title.getText());
        pickingProductCardData.setPrice(ParserUtil.strToDouble(price.getText()));
        pickingProductCardData.setWeight(ParserUtil.strToDouble(weight.getText(), "."));
        pickingProductCardData.setReasonOfLack(productCardReason.getTextIfPresent());
        pickingProductCardData.setStockQuantity(ParserUtil.strToInt(stockQuantity.getText()));
        pickingProductCardData.setCollectedQuantity(ParserUtil.strToInt(collectedQuantityFld.getText()));
        pickingProductCardData.setOrderedQuantity(ParserUtil.strToInt(orderedQuantityFld.getText()));
        return pickingProductCardData;
    }
}
