package com.leroy.magportal.api.tests;

import static com.leroy.magportal.api.constants.OnlineOrderTypeConst.DELIVERY_TO_DOOR;
import static com.leroy.magportal.ui.constants.TestDataConstants.SIMPLE_CUSTOMER_DATA_1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.core.api.ThreadApiClient;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.clients.Is4AuthClient;
import com.leroy.magmobile.api.data.oauth.Is4TokenData;
import com.leroy.magportal.api.clients.CatalogSearchClient;
import com.leroy.magportal.api.constants.OnlineOrderTypeConst.OnlineOrderTypeData;
import com.leroy.magportal.api.helpers.BitrixHelper;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.ui.models.customers.SimpleCustomerData;
import com.leroy.umbrella_extension.authorization.AuthClient;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.tunnel.TunnelClient;
import ru.leroymerlin.qa.core.clients.tunnel.data.BitrixSolutionPayload;
import ru.leroymerlin.qa.core.clients.tunnel.data.BitrixSolutionResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Тестовый класс надо будет переименовать и дать соответствующее название
public class SomeTest extends BaseMagPortalApiTest {

  @Inject
  CatalogSearchClient paymentHelper;
  @Inject
  private BitrixHelper bitrixHelper;
  @Inject
  private AuthClient authClient;
  @Inject
  private Is4AuthClient is4AuthClient;

  @Test(description = "C1 Название теста")
  public void test() throws Exception {
    //String code = authClient.authAndGetCode(EnvConstants.BASIC_USER_LDAP, EnvConstants.BASIC_USER_PASS);
    //Response<Is4TokenData> response = is4AuthClient.sendPostCodeRequest(code);
    bitrixHelper.createOnlineOrders(2, DELIVERY_TO_DOOR, 2);
//        String id = bitrixHelper.createOnlineOrder();
//        paymentHelper.makePaymentCard(id);
  }

  /**
   * Создает ONLINE ордер
   *
   * @return task id
   */

}