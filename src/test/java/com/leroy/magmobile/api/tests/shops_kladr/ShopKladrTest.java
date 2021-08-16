package com.leroy.magmobile.api.tests.shops_kladr;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import com.google.inject.Inject;
import com.leroy.magmobile.api.clients.ShopKladrClient;
import com.leroy.magmobile.api.data.kladr.KladrItemData;
import com.leroy.magmobile.api.data.kladr.KladrItemDataList;
import com.leroy.magmobile.api.data.shops.ShopData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import com.leroy.magportal.api.clients.ShopsClient;
import io.qameta.allure.AllureId;
import org.testng.annotations.Test;
import org.testng.util.Strings;
import ru.leroymerlin.qa.core.clients.base.Response;

public class ShopKladrTest extends BaseProjectApiTest {

    @Inject
    private ShopKladrClient shopKladrClient;
    @Inject
    private ShopsClient shopsClient;

    @Test(description = "C23195091 GET shops")
    @AllureId("13233")
    public void testGetShops() {
        Response<ShopData> resp = shopKladrClient.getShops();
        shopsClient.assertGetShopsResult(resp);
    }

    @Test(description = "C3165821 Kladr gets a city that exists")
    @AllureId("13229")
    public void testKladrGetsExistedCity() {
        int limit = 12;
        Response<KladrItemDataList> resp = shopKladrClient.getKladrByCity("2900000000000", 12);
        assertThat(resp, successful());
        KladrItemDataList dataList = resp.asJson();
        assertThat("total count", dataList.getTotalCount(), is(limit));
        assertThat("items count", dataList.getItems(), hasSize(limit));
        for (KladrItemData kladrItemData : dataList.getItems()) {
            String desc = String
                    .format("RegionId: %s, No value for field: ", kladrItemData.getId());
            softAssert().isTrue(Strings.isNotNullAndNotEmpty(kladrItemData.getId()), desc + "id");
            softAssert()
                    .isTrue(Strings.isNotNullAndNotEmpty(kladrItemData.getLabel()), desc + "label");
            softAssert()
                    .isTrue(Strings.isNotNullAndNotEmpty(kladrItemData.getValue()), desc + "value");
        }
        softAssert().verifyAll();
    }

}
