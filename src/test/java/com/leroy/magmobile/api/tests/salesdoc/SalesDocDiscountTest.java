package com.leroy.magmobile.api.tests.salesdoc;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.leroy.magmobile.api.clients.MagMobileClient;
import com.leroy.magmobile.api.data.sales.SalesDocDiscountData;
import com.leroy.magmobile.api.data.sales.DiscountReasonData;
import com.leroy.magmobile.api.requests.salesdoc.discount.GetSalesDocDiscount;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class SalesDocDiscountTest extends BaseProjectApiTest {

    @Inject
    private Provider<MagMobileClient> magMobileClient;

    private String productLmCode;

    @Override
    protected boolean isNeedAccessToken() {
        return false;
    }

    @BeforeClass
    private void setUp() {
        productLmCode = getCatalogSearchClient().getProductLmCodes(1).get(0);
    }

    @Test(description = "C3254680 SalesDoc GET discounts")
    public void testSalesDocGetDiscounts() {
        Response<SalesDocDiscountData> resp = magMobileClient.get().getSalesDocDiscount(new GetSalesDocDiscount()
                .setLmCode(productLmCode)
                .setShopId(sessionData.getUserShopId()));
        assertThat(resp, successful());
        SalesDocDiscountData salesDocDiscountData = resp.asJson();
        assertThat("maxDiscount", salesDocDiscountData.getMaxDiscount(), greaterThan(0.0));
        List<DiscountReasonData> discountReasonDataList = salesDocDiscountData.getReasons();
        assertThat("Reasons size", discountReasonDataList.size(), greaterThanOrEqualTo(6));
        for (DiscountReasonData discountReasonData : discountReasonDataList) {
            assertThat("Reason Id", discountReasonData.getId(), allOf(notNullValue(), greaterThan(0)));
            assertThat("Reason name", discountReasonData.getName(), not(isEmptyOrNullString()));
        }
    }

}
