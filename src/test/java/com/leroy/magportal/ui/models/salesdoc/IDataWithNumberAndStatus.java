package com.leroy.magportal.ui.models.salesdoc;

public interface IDataWithNumberAndStatus<D> {

    String getNumber();

    String getStatus();

    void assertEqualsNotNullExpectedFields(D expectedData);
}
