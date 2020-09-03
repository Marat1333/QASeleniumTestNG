package com.leroy.magportal.api.clients;

import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magportal.api.requests.order.OrderGet;
import io.qameta.allure.Step;
import ru.leroymerlin.qa.core.clients.base.Response;

public class OrderClient extends com.leroy.magmobile.api.clients.OrderClient {

  @Step("Get order with id = {orderId}")
  public Response<OrderData> getOrder(String orderId) {
    OrderGet req = new OrderGet();
    req.setOrderId(orderId);
    return execute(req, OrderData.class);
  }

}
