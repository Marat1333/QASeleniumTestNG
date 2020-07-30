package com.leroy.magportal.ui.pages.orders;

import com.leroy.core.annotations.Form;
import com.leroy.magportal.ui.pages.customers.form.CustomerSearchForm;

public class OrderDraftPage extends OrderHeaderPage {

    @Form
    CustomerSearchForm customerSearchForm;

    public CustomerSearchForm getCustomerSearchForm() {
        return customerSearchForm;
    }
}
