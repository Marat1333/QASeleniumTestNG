package com.leroy.magportal.ui.pages.picking.widget;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.fieldfactory.CustomLocator;
import com.leroy.core.web_elements.general.Button;
import com.leroy.magportal.ui.constants.picking.PickingConst;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import io.qameta.allure.Step;
import java.util.Arrays;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;


public class PickingFilterWidget extends MagPortalBasePage {

    private final String BASE_FILTER_XPATH = "//div[contains(@class, 'additionalFilter-content')]";

    @WebFindBy(xpath = "//div[contains(@class, 'ScreenHeader-MainContent')]//button[contains(@class, 'AdditonalFilterOpenBtn')]", metaName = "Кнопка открытия фильтров")
    Button filterOpenBtn;

    @WebFindBy(xpath = BASE_FILTER_XPATH + "//button[contains(@class, 'closeButton')]", metaName = "Кнопка закрытия фильтров")
    Button filterCloseBtn;

    @WebFindBy(xpath = BASE_FILTER_XPATH + "/div[2]/div/div/div/div[2]/div[1]/button", metaName = "Кнопка 'Показать фильтры'") //TODO Переделать потом на айди
    Button showResultsBtn;

    @WebFindBy(xpath = BASE_FILTER_XPATH + "/div[2]/div/div/div/div[2]/div[2]/button", metaName = "Кнопка очистки фильтров") //TODO Переделать потом на айди
    Button clearFiltersBtn;

    public PickingFilterWidget(){
        if(filterOpenBtn.isVisible()){
            openFilters();
        }
    }

    @Step("Открытие виджета фильтров")
    public void openFilters() {
        filterOpenBtn.click();
    }

    @Step("Закрытие виджета фильтров")
    public void closeFilters() {
        filterCloseBtn.click();
    }

    @Step("Очистка виджета фильтров")
    public void clearFilters() {
        clearFiltersBtn.click();
    }

    @SneakyThrows
    public Button getAssemblyTypeButton(@NotNull PickingConst.AssemblyType type) {
        String xpath = BASE_FILTER_XPATH + "/div[2]/div/div/div/div[1]/div[2]"; //TODO Переделать потом на айди

        switch (type) {
            case SHOPPING_ROOM: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[1]")));
            case STOCK: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[2]")));
            case SS: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[3]")));
        }
        throw new Exception("Не передан тип сборки для фильтров, передан " + type);
    }

    @SneakyThrows
    public Button getPickingStatusButton(@NotNull PickingConst.PickingStatus status) {
        String xpath = BASE_FILTER_XPATH + "/div[2]/div/div/div/div[1]/div[3]"; //TODO Переделать потом на айди

        switch (status) {
            case READY_TO_PICKING: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[1]")));
            case PICKING: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[2]")));
            case PICKING_PAUSE: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[3]")));
            case PARTIALLY_PICKED: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[4]")));
            case PICKED: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[5]")));
        }
        throw new Exception("Не передан статус сборки для фильтров, передан " + status);
    }

    public Button getDepartmentButton(@NotNull String name){
        String xpath = BASE_FILTER_XPATH + "/div[2]/div/div/div/div[1]/div[4]/button//span[contains(text(), '" + name + "')]"; //TODO Переделать потом на айди

        return new Button(driver, new CustomLocator(By.xpath(xpath)));
    }

    @SneakyThrows
    public Button getOrderTypeButton(@NotNull PickingConst.OrderType type) {
        String xpath = BASE_FILTER_XPATH + "/div[2]/div/div/div/div[1]/div[5]"; //TODO Переделать потом на айди

        switch (type) {
            case ONLINE: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[1]")));
            case OFFLINE: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[2]")));
        }
        throw new Exception("Не передан тип заказа для фильтров, передан " + type);
    }

    @SneakyThrows
    public Button getClientTypeButton(@NotNull PickingConst.ClientType type) {
        String xpath = BASE_FILTER_XPATH + "/div[2]/div/div/div/div[1]/div[6]"; //TODO Переделать потом на айди

        switch (type) {
            case ENTITY: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[1]")));
            case CLIENT: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[2]")));
            case PROFI: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[3]")));
        }
        throw new Exception("Не передан тип клиента для фильтров, передан " + type);
    }

    @SneakyThrows
    public Button getDeliveryTypeButton(@NotNull PickingConst.DeliveryType deliveryType) {
        String xpath = BASE_FILTER_XPATH + "/div[2]/div/div/div/div[1]/div[7]"; //TODO Переделать потом на айди

        switch (deliveryType) {
            case PICKUP: return new Button(driver, new CustomLocator(By.xpath(xpath + "//button[1]")));
            case DELIVERY_TK: return new Button(driver, new CustomLocator(By.xpath(xpath + "//button[2]")));
            case DELIVERY_PVZ: return new Button(driver, new CustomLocator(By.xpath(xpath + "//button[3]")));
            case DELIVERY_KK: return new Button(driver, new CustomLocator(By.xpath(xpath + "//button[4]")));
        }
        throw new Exception("Не передан способ получения для фильтров, передан " + deliveryType);
    }

    @SneakyThrows
    public Button getTagButton(@NotNull PickingConst.Tag tag) {
        String xpath = BASE_FILTER_XPATH + "/div[2]/div/div/div/div[1]/div[8]"; //TODO Переделать потом на айди

        switch (tag) {
            case DEBT: return new Button(driver, new CustomLocator(By.xpath(xpath + "//button[1]")));
            case RISK_OF_NOT_COLLECT: return new Button(driver, new CustomLocator(By.xpath(xpath + "//button[2]")));
            case RISK_NOT_TO_SHIP: return new Button(driver, new CustomLocator(By.xpath(xpath + "//button[3]")));
            case PICKING_TODAY: return new Button(driver, new CustomLocator(By.xpath(xpath + "//button[4]")));
            case FOR_FUTURE_DATES: return new Button(driver, new CustomLocator(By.xpath(xpath + "//button[5]")));
        }
        throw new Exception("Не передан тэг для фильтров, передан " + tag);
    }

    @SneakyThrows
    public Button getSaleSchemeButton(@NotNull PickingConst.SaleScheme scheme) {
        String xpath = BASE_FILTER_XPATH + "/div[2]/div/div/div/div[1]/div[9]"; //TODO Переделать потом на айди

        switch (scheme) {
            case LT: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[1]")));
            case LTD: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[2]")));
            case SLT: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[3]")));
            case SLTD: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[4]")));
            case SLTX: return new Button(driver, new CustomLocator(By.xpath(xpath + "/button[5]")));
        }
        throw new Exception("Не передана схема продажи для фильтров, передано " + scheme);
    }

    @Step("Выбор типа сборки в фильтре: {assemblyTypes}")
    public PickingFilterWidget clickPickingTypeFilters(PickingConst.AssemblyType... assemblyTypes) {
        Arrays.stream(assemblyTypes).forEach((p) -> getAssemblyTypeButton(p).click());
        return this;
    }

    @Step("Выбор статуса сборки в фильтре: {pickingStatuses}")
    public PickingFilterWidget clickPickingStatusFilters(PickingConst.PickingStatus... pickingStatuses) {
        Arrays.stream(pickingStatuses).forEach((p) -> getPickingStatusButton(p).click());
        return this;
    }

    @Step("Выбор отдела в фильтре: {departments}")
    public PickingFilterWidget clickDepartmentFilters(String... departments) {
        Arrays.stream(departments).forEach((p) -> getDepartmentButton(p).click());
        return this;
    }

    @Step("Выбор типа заказа в фильтре: {orderTypes}")
    public PickingFilterWidget clickOrderTypesFilters(PickingConst.OrderType... orderTypes) {
        Arrays.stream(orderTypes).forEach((p) -> getOrderTypeButton(p).click());
        return this;
    }

    @Step("Выбор типа клиента в фильтре: {clientTypes}")
    public PickingFilterWidget clickClientTypeFilters(PickingConst.ClientType... clientTypes) {
        Arrays.stream(clientTypes).forEach((p) -> getClientTypeButton(p).click());
        return this;
    }

    @Step("Выбор схемы продажи в фильтре: {saleSchemes}")
    public PickingFilterWidget clickSaleSchemeFilters(PickingConst.SaleScheme... saleSchemes) {
        Arrays.stream(saleSchemes).forEach((p) -> getSaleSchemeButton(p).click());
        return this;
    }

    @Step("Выбор способов получения в фильтре: {deliveryTypes}")
    public PickingFilterWidget clickReceivingMethodFilters(PickingConst.DeliveryType... deliveryTypes) {
        Arrays.stream(deliveryTypes).forEach((p) -> getDeliveryTypeButton(p).click());
        return this;
    }

    @Step("Выбор тэгов в фильтре: {tags}")
    public PickingFilterWidget clickTagFilters(PickingConst.Tag... tags) {
        Arrays.stream(tags).forEach((p) -> getTagButton(p).click());
        return this;
    }

}
