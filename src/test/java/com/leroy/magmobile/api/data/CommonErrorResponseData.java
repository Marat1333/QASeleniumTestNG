package com.leroy.magmobile.api.data;

import lombok.Data;

@Data
public class CommonErrorResponseData {
    private String error;
    private CommonValidationData validation;
}
