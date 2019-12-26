package com.leroy.pages.web;

import com.leroy.constants.Gender;
import com.leroy.core.TestContext;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.models.CustomerData;
import com.leroy.pages.web.common.MenuPage;
import org.apache.commons.lang3.StringUtils;

public class CustomerPersonalInfoPage extends MenuPage {

    public CustomerPersonalInfoPage(TestContext context) {
        super(context);
    }

    @WebFindBy(xpath = "//h4[contains(@class, 'Header')]",
            metaName = "Основной заголовок страницы - 'Клиенты'")
    Element headerLbl;

    @WebFindBy(xpath = "//h5[contains(@class, 'Header')]",
            metaName = "Заголовок страницы с именем клиента")
    Element headerClientNameLbl;

    @WebFindBy(xpath = "//div/p[contains(.,'Пол')]/following-sibling::p[1]")
    Element genderObj;

    @WebFindBy(xpath = "//div/p[contains(.,'Телефон')]/following-sibling::p[1]")
    Element phoneObj;

    @WebFindBy(xpath = "//div/p[contains(.,'Email')]/following-sibling::p[1]")
    Element emailObj;

    @WebFindBy(xpath = "//div/p[contains(.,'Офис')]/following-sibling::p[1]")
    Element officeObj;

    @WebFindBy(xpath = "//div[contains(@class, 'CustomerList__items')]/span/div",
            metaName = "Мои недавние клиенты")
    ElementList<Element> myRecentlyCustomers;

    @Override
    public void waitForPageIsLoaded() {
        headerLbl.waitUntilTextIsEqualTo("Клиенты");
    }

    // Verifications
    public CustomerPersonalInfoPage shouldCustomerDataOnPageIs(CustomerData data) {
        if (data.getFirstName() != null)
            softAssert.isEquals(headerClientNameLbl.getText(), StringUtils.capitalize(data.getFirstName()),
                    "Имя клиента должно быть %s");
        if (data.getGender() != null)
            softAssert.isEquals(genderObj.getText(), (data.getGender().equals(Gender.MALE) ? "Мужской" : "Женский"),
                    "Пол должен быть %s");
        if (data.getWorkPhone() != null)
            softAssert.isEquals(phoneObj.getText(), "+7" + data.getWorkPhone(),
                    "Основной телефон должен быть %s");
        if (data.getPersonalPhone() != null)
            softAssert.isEquals(phoneObj.getText(), "+7" + data.getPersonalPhone(),
                    "Основной телефон должен быть %s");
        if (data.getPersonalEmail() != null)
            softAssert.isEquals(emailObj.getText(), data.getPersonalEmail(),
                    "Email должен быть %s");
        else if (data.getWorkEmail() != null)
            softAssert.isEquals(emailObj.getText(), data.getWorkEmail(),
                    "Email должен быть %s");
        else
            softAssert.isEquals(emailObj.getText(), "Не указано",
                    "Email должен быть '%s'");
        softAssert.isEquals(officeObj.getText(), "Не указано",
                "Офис должен быть '%s'");
        softAssert.verifyAll();
        return this;
    }

    public CustomerPersonalInfoPage shouldMyRecentlyCustomerIs(int index, String name, String phone)
            throws Exception {
        Element customerElem = myRecentlyCustomers.get(index);
        softAssert.isEquals(customerElem.findChildElement("./div/span").getText(), StringUtils.capitalize(name),
                "Имя " + index + "-ого клиента должно быть %s из списка 'Мои недавние клиенты'");
        softAssert.isEquals(customerElem.findChildElement("./div/div/span").getText(), StringUtils.capitalize(
                name.substring(0, 1)),
                "Первая буква имени " + index + "-ого клиента должна быть %s из списка 'Мои недавние клиенты'");
        String expectedPhone = phone.length() != 10 ? phone : String.format("+7 %s %s-%s-%s",
                phone.substring(0, 3), phone.substring(3, 6), phone.substring(6, 8), phone.substring(8, 10));
        softAssert.isEquals(customerElem.findChildElement("./div[2]/span").getText(), expectedPhone,
                "Телефон " + index + "-ого клиента должно быть %s из списка 'Мои недавние клиенты'");
        softAssert.verifyAll();
        return this;
    }

}
