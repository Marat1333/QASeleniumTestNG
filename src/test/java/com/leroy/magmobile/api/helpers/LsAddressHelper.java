package com.leroy.magmobile.api.helpers;

import com.google.inject.Inject;
import com.leroy.magmobile.api.clients.LsAddressClient;
import com.leroy.magmobile.api.data.address.AlleyData;
import com.leroy.magmobile.api.data.address.AlleyDataItems;
import com.leroy.magportal.api.helpers.BaseHelper;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static org.hamcrest.MatcherAssert.assertThat;

public class LsAddressHelper extends BaseHelper {

    @Inject
    private LsAddressClient lsAddressClient;

    @Step("Search alley by id")
    public AlleyData searchAlleyById(int alleyId, boolean allowEmpty) {
        Response<AlleyDataItems> resp = lsAddressClient.searchForAlleys();
        assertThat("Catalog search request has failed.", resp, successful());
        List<AlleyData> items = resp.asJson().getItems();
        for (AlleyData item : items) {
            if (item.getId() == alleyId) {
                return item;
            }
        }
        if (allowEmpty) {
            return new AlleyData();
        } else {
            assertThat("Required alley not found by id", false);
            return null;
        }
    }

    @Step("Search alley by id")
    public AlleyData searchAlleyById(int alleyId) {
        return searchAlleyById(alleyId, false);
    }
}
