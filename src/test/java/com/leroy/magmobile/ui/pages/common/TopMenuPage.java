package com.leroy.magmobile.ui.pages.common;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magmobile.ui.Context;
import com.leroy.magmobile.ui.pages.sales.MainSalesDocumentsPage;

public class TopMenuPage extends BottomMenuPage {

    public TopMenuPage(Context context) {
        super(context);
    }

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

    public MainSalesDocumentsPage goToSalesDocumentsSection() {
        salesDocumentsBtn.click();
        return new MainSalesDocumentsPage(context);
    }

    public void goToClientsSection() {
        clientsBtn.click();
    }

}
