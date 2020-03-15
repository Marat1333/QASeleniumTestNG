package com.leroy.magmobile.api.tests.salesdoc;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.core.SessionData;
import com.leroy.magmobile.api.helpers.FindTestDataHelper;
import com.leroy.magmobile.api.tests.common.BaseProjectTest;
import com.leroy.magmobile.models.search.FiltersData;
import com.leroy.magmobile.ui.pages.search.FilterPage;
import com.leroy.umbrella_extension.magmobile.MagMobileClient;
import com.leroy.umbrella_extension.magmobile.data.sales.DiscountData;
import com.leroy.umbrella_extension.magmobile.data.sales.DiscountReasonData;
import com.leroy.umbrella_extension.magmobile.requests.salesdoc.GetSalesDocDiscount;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class SalesDocDiscountTest extends BaseProjectTest {

    @Inject
    private Provider<MagMobileClient> magMobileClient;

    private String productLmCode;

    @BeforeClass
    private void setUpDefaultSessionData() {
        sessionData = new SessionData();
        sessionData.setUserShopId("35");
        sessionData.setUserDepartmentId("1");
        productLmCode = FindTestDataHelper.getProducts(magMobileClient.get(), sessionData, 1,
                new FiltersData(FilterPage.MY_SHOP_FRAME_TYPE)).get(0).getLmCode();
    }

    @Test(description = "C3254680 SalesDoc GET discounts")
    public void testSalesDocGetDiscounts() {
        Response<DiscountData> resp = magMobileClient.get().getSalesDocDiscount(new GetSalesDocDiscount()
                .setLmCode(productLmCode)
                .setShopId(sessionData.getUserShopId()));
        assertThat(resp, successful());
        DiscountData discountData = resp.asJson();
        assertThat("maxDiscount", discountData.getMaxDiscount(), greaterThan(0.0));
        List<DiscountReasonData> discountReasonDataList = discountData.getReasons();
        assertThat("Reasons size", discountReasonDataList.size(), greaterThanOrEqualTo(6));
        for (DiscountReasonData discountReasonData : discountReasonDataList) {
            assertThat("Reason Id", discountReasonData.getId(), allOf(notNullValue(), greaterThan(0)));
            assertThat("Reason name", discountReasonData.getName(), not(isEmptyOrNullString()));
        }
    }

}
