package com.leroy.magmobile.ui.models.customer;

import com.leroy.core.ContextProvider;
import com.leroy.core.asserts.SoftAssertWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class MagLegalCustomerData {
    private String orgName;
    private String contractNumber;
    private String orgPhone;
    private String orgCard;

    private MagCustomerData chargePerson;

    public void assertEqualsNotNullExpectedFields(MagLegalCustomerData expectedCustomerData) {
        SoftAssertWrapper softAssert = ContextProvider.getContext().getSoftAssert();
        if (expectedCustomerData.getOrgName() != null) {
            softAssert.isEquals(orgName, expectedCustomerData.getOrgName(),
                    "Неверное название организации");
        }
        if (expectedCustomerData.getContractNumber() != null) {
            softAssert.isEquals(contractNumber, expectedCustomerData.getContractNumber(),
                    "Неверный номер договора организации");
        }
        if (expectedCustomerData.getOrgPhone() != null) {
            softAssert.isEquals(orgPhone, expectedCustomerData.getOrgPhone(),
                    "Неверный номер телефона организации");
        }
        if (expectedCustomerData.getChargePerson() != null)
            chargePerson.assertEqualsNotNullExpectedFields(expectedCustomerData.getChargePerson());
        softAssert.verifyAll();
    }

}
