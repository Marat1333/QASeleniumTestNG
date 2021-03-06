package com.leroy.magportal.ui.pages.customers;

import com.leroy.constants.Gender;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Element;
import com.leroy.core.web_elements.general.ElementList;
import com.leroy.magportal.ui.models.customers.CustomerWebData;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;

public class CustomerPersonalInfoPage extends MagPortalBasePage {

    public static final String HEADER = "Клиенты";

    @WebFindBy(xpath = "//span[text()='" + HEADER + "']",
            metaName = "Основной заголовок страницы - 'Клиенты'")
    Element headerLbl;

    @WebFindBy(xpath = "//h5[contains(@class, 'Header')]",
            metaName = "Заголовок страницы с именем клиента")
    Element headerClientNameLbl;

    @WebFindBy(xpath = "//div/p[contains(.,'Пол')]/following-sibling::p[1]", metaName = "Пол клиента")
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
        headerClientNameLbl.waitForVisibility();
        genderObj.waitForVisibility(tiny_timeout);
        headerLbl.waitUntilTextIsEqualTo(HEADER, timeout);
    }

    @Step("Проверить, что страница с персональными данными клиента отображается корректно")
    public CustomerPersonalInfoPage verifyRequiredElements() {
        softAssert.isElementVisible(headerLbl);
        softAssert.isElementVisible(genderObj);
        softAssert.verifyAll();
        return this;
    }

    // Verifications
    @Step("Проверить, что на странице отображается следующая информация о клиенте: {data}")
    public CustomerPersonalInfoPage shouldCustomerDataOnPageIs(CustomerWebData data) {
        if (data.getFirstName() != null)
            softAssert.isEquals(headerClientNameLbl.getText(), StringUtils.capitalize(data.getFirstName()),
                    "Неверное Имя клиента");
        if (data.getGender() != null)
            softAssert.isEquals(genderObj.getText(), (data.getGender().equals(Gender.MALE) ? "Мужской" : "Женский"),
                    "Неверный Пол");
        if (data.getPhoneNumber() != null)
            softAssert.isEquals(phoneObj.getText(), data.getPhoneNumber(),
                    "Неверный Основной телефон");
        if (data.getEmail() != null)
            softAssert.isEquals(emailObj.getText(), data.getEmail(),
                    "Неверный Email");
        else
            softAssert.isEquals(emailObj.getText(), "Не указано",
                    "Неверный Email");
        softAssert.isEquals(officeObj.getText(), "Не указано",
                "Неверный Офис");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что среди недавно выбранных клиентов {index}-ый с именем {name} и телефоном {phone}")
    public CustomerPersonalInfoPage shouldMyRecentlyCustomerIs(int index, String name, String phone)
            throws Exception {
        anAssert.isTrue(myRecentlyCustomers.getCount() > 0, "Не отображаются 'Недавние клиенты'");
        Element customerElem = myRecentlyCustomers.get(index);
        softAssert.isEquals(customerElem.findChildElement("./div/span").getText(), StringUtils.capitalize(name),
                "Имя " + index + "-ого клиента неверно из списка 'Мои недавние клиенты'");
        String expectedPhone = phone.length() != 10 ? phone : String.format("+7 %s %s-%s-%s",
                phone.substring(0, 3), phone.substring(3, 6), phone.substring(6, 8), phone.substring(8, 10));
        softAssert.isEquals(customerElem.findChildElement("./div[3]/span").getText().replaceAll("[ -]", ""), expectedPhone,
                "Телефон " + index + "-ого клиента должно быть %s из списка 'Мои недавние клиенты'");
        softAssert.verifyAll();
        return this;
    }

}
