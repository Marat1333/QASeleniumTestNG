package com.leroy.magmobile.ui.pages.common;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.pages.customers.MainCustomerPage;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;
import io.qameta.allure.Step;

public abstract class TopMenuPage extends BottomMenuPage {

    private static final String TYPICAL_BUTTON_XPATH = "(//android.view.ViewGroup[@content-desc='Button'])";

    @AppFindBy(xpath = TYPICAL_BUTTON_XPATH + "[1]")
    private Element productAndServicesBtn;

    @AppFindBy(xpath = TYPICAL_BUTTON_XPATH + "[2]")
    private Element calculationsBtn;

    @AppFindBy(xpath = TYPICAL_BUTTON_XPATH + "[3]")
    private Element salesDocumentsBtn;

    @AppFindBy(xpath = TYPICAL_BUTTON_XPATH + "[4]")
    private Element clientsBtn;

    /* ------------------------- ACTION STEPS -------------------------- */

    public void goToProductAndServicesSection() {
        productAndServicesBtn.click();
    }

    public void goToCalculationsSection() {
        calculationsBtn.click();
    }

    @Step("Перейти в раздел 'Документы продажи'")
    public MainSalesDocumentsPage goToSalesDocumentsSection() {
        salesDocumentsBtn.click();
        return new MainSalesDocumentsPage();
    }

    @Step("Перейти в раздел 'Клиенты'")
    public MainCustomerPage goToClientsSection() {
        clientsBtn.click();
        return new MainCustomerPage();
    }

}
