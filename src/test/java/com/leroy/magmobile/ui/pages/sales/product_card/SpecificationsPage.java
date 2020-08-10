package com.leroy.magmobile.ui.pages.sales.product_card;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magmobile.api.data.catalog.Characteristic;
import com.leroy.magmobile.api.data.catalog.product.CatalogProductData;
import com.leroy.magmobile.ui.pages.sales.product_card.prices_stocks_supplies.SuppliesPage;
import io.qameta.allure.Step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpecificationsPage extends ProductCardPage {
    @AppFindBy(text = "Поставщик")
    Button supplyInfoNavigationBtn;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.view.ViewGroup/android.widget.TextView[2]/..")
    ElementList<Element> characteristicPairs;

    @AppFindBy(xpath = "//android.widget.ScrollView/android.view.ViewGroup/android.widget.TextView")
    Element description;

    @AppFindBy(xpath = "//android.widget.ScrollView")
    AndroidScrollView<String> mainScrollView;

    @Step("Перейти на страницу с информацией о поставщике")
    public SuppliesPage goToSupplyInfoPage() {
        supplyInfoNavigationBtn.click();
        return new SuppliesPage();
    }

    @Step("Проверить корректность данных")
    public SpecificationsPage shouldDataIsCorrect(CatalogProductData data) throws Exception {
        HashMap<String, String> frontCharacteristicsMap = grabCharacteristicsFromPage();
        List<Characteristic> characteristicList = data.getCharacteristics();
        HashMap<String, String> dataCharacteristicsMap = new HashMap<>();
        for (Characteristic characteristic : characteristicList) {
            dataCharacteristicsMap.put(characteristic.getName(), characteristic.getValue());
        }
        anAssert.isEquals(frontCharacteristicsMap, dataCharacteristicsMap, "keys mismatch");

        List<String> frontValues = new ArrayList<>(frontCharacteristicsMap.values());
        List<String> dataValues = new ArrayList<>(dataCharacteristicsMap.values());
        anAssert.isEquals(frontValues, dataValues, "values mismatch");
        anAssert.isEquals(data.getDescription(), description.getText(), "description");
        return this;
    }

    @Step("Проверить отсутствие кнопки \"Поставщик\"")
    public SpecificationsPage shouldSupplierBtnIsInvisible(){
        anAssert.isElementNotVisible(supplyInfoNavigationBtn);
        return this;
    }

    private HashMap grabCharacteristicsFromPage() throws Exception {
        HashMap<String, String> characteristicsMap = new HashMap<>();
        int tmpSize = 0;
        int i = 0;
        while (i != 20) {
            for (Element tmp : characteristicPairs) {
                characteristicsMap.put(tmp.findChildElement("./*[1]").getText(), tmp.findChildElement("./*[2]").getText());
            }
            if (tmpSize == characteristicsMap.size()) {
                break;
            }
            tmpSize = characteristicsMap.size();
            mainScrollView.scrollDown();
            i++;
        }
        return characteristicsMap;
    }
}
