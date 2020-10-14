package com.leroy.magportal.ui.tests.pao.order;

import com.google.inject.Inject;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.helpers.BitrixHelper;
import com.leroy.magportal.ui.pages.orders.OrderHeaderPage;
import com.leroy.magportal.ui.tests.BasePAOTest;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.tunnel.data.BitrixSolutionResponse;

public class SizOrderTest extends BasePAOTest {

    @Inject
    private BitrixHelper bitrixHelper;

    @Test(description = "C23399998 Признаки ТК в листинге, на вкладке заказа")
    public void testTKSignsOnOrderTab() throws Exception {
        //OnlineOrderTypeConst.OnlineOrderTypeData currentOrderType = OnlineOrderTypeConst.DELIVERY_TK;
        //BitrixSolutionResponse response = bitrixHelper.createOnlineOrder(currentOrderType);
        String orderId = "201003127990";

        OrderHeaderPage orderHeaderPage = loginSelectShopAndGoTo(OrderHeaderPage.class);
        String s = "";

    }

}
