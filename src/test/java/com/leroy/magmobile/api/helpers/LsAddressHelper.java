package com.leroy.magmobile.api.helpers;

import com.leroy.magmobile.api.data.address.AlleyData;
import com.leroy.magmobile.api.data.address.AlleyDataItems;
import com.leroy.magportal.api.helpers.BaseHelper;
import io.qameta.allure.Step;
import org.testng.Assert;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

public class LsAddressHelper extends BaseHelper {

    @Step("Search alley by id")
    public AlleyData searchAlleyById(Response<AlleyDataItems> resp, int alleyId) {
        List<AlleyData> items = resp.asJson().getItems();
        for (AlleyData item : items) {
            if (item.getId() == alleyId) {
                return item;
            }
        }

        return new AlleyData();
    }
}
