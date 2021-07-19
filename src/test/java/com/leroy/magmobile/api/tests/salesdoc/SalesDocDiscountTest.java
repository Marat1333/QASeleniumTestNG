package com.leroy.magmobile.api.tests.salesdoc;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

import com.google.inject.Inject;
import com.leroy.common_mashups.helpers.SearchProductHelper;
import com.leroy.magmobile.api.clients.SalesDocProductClient;
import com.leroy.magmobile.api.data.sales.DiscountReasonData;
import com.leroy.magmobile.api.data.sales.SalesDocDiscountData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import java.util.List;

import io.qameta.allure.TmsLink;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.util.Strings;
import ru.leroymerlin.qa.core.clients.base.Response;


public class SalesDocDiscountTest extends BaseProjectApiTest {

    @Inject
    private SearchProductHelper searchProductHelper;
    @Inject
    private SalesDocProductClient salesDocProductClient;

    private String productLmCode;

    @Override
    protected boolean isNeedAccessToken() {
        return false;
    }

    @BeforeClass
    private void setUp() {
        productLmCode = searchProductHelper.getProductLmCode();
    }

    @Test(description = "C3254680 SalesDoc GET discounts")
    @TmsLink("3261")
    public void testSalesDocGetDiscounts() {
        Response<SalesDocDiscountData> resp = salesDocProductClient.getSalesDocDiscountByLmCode(productLmCode);
        assertThat(resp, successful());
        SalesDocDiscountData salesDocDiscountData = resp.asJson();
        assertThat("maxDiscount", salesDocDiscountData.getMaxDiscount(), greaterThan(0.0));
        List<DiscountReasonData> discountReasonDataList = salesDocDiscountData.getReasons();
        assertThat("Reasons size", discountReasonDataList.size(), greaterThanOrEqualTo(6));
        for (DiscountReasonData discountReasonData : discountReasonDataList) {
            assertThat("Reason Id", discountReasonData.getId(), allOf(notNullValue(), greaterThan(0)));
            assertThat("Reason name", Strings.isNotNullAndNotEmpty(discountReasonData.getName()));
        }
    }

}
