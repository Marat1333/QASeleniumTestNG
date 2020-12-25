package com.leroy.magportal.api.tests;

import com.google.inject.Inject;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.helpers.OnlineOrderHelper;
import com.leroy.umbrella_extension.aemtunnel.AemTunnelClient;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.gagarin.GagarinClient;
import ru.leroymerlin.qa.core.clients.tunnel.TunnelClient;

public class DebugTest extends BaseMagPortalApiTest {

    @Inject
    private AemTunnelClient aemTunnelClient;
    @Inject
    private TunnelClient tunnelClient;
    @Inject
    private OnlineOrderHelper onlineOrderHelper;
    @Inject
    private GagarinClient gagarinClient;

    @Test(description = "C3182981 Get Shops List")
    public void testGetShops() {

//        tunnelClient.
//        Response<?> response = gagarinClient.getStoreByRegion(34);
        OnlineOrderTypeData onlineOrderTypeConst = OnlineOrderTypeConst.DELIVERY_TO_DOOR;
        onlineOrderTypeConst.setShopId("16");
        onlineOrderHelper.createDimensionalOnlineOrder(onlineOrderTypeConst);
    }
}
