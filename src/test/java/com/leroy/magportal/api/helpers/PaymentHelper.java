package com.leroy.magportal.api.helpers;

import com.google.inject.Inject;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.requests.payments.PaymentStatusEnum;
import java.util.List;
import org.testng.Assert;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.payment.PaymentClient;
import ru.leroymerlin.qa.core.clients.payment.data.task.ChangeStatus;
import ru.leroymerlin.qa.core.clients.payment.data.task.Link;
import ru.leroymerlin.qa.core.clients.payment.data.task.PaymentTask;

public class PaymentHelper extends BaseHelper {

  private OrderClient orderClient;
  private ChangeStatus changeStatus;
  private String paymentTaskId;
  private List<Link> links;

  @Inject
  private PaymentClient paymentClient;

  public String getPaymentTaskId(String orderId) {
    orderClient = getOrderClient();
    Response<OrderData> resp = orderClient.getOrder(orderId);
    if (!resp.isSuccessful()) {
      int tryCount = 3;
      for (int i = 0; i < tryCount; i++) {
        resp = orderClient.getOrder(orderId);
        if (resp.isSuccessful())
          break;
      }
    }    Assert.assertTrue(resp.isSuccessful(),
        "API: Impossible to get Order. Response: " + resp.toString());
    return resp.asJson().getPaymentTaskId();
  }


  public Response<PaymentTask> updatePayment(String orderId, PaymentStatusEnum status) {
    changeStatus = new ChangeStatus();
    paymentTaskId = getPaymentTaskId(orderId);
    changeStatus.setUpdatedBy(userSessionData().getUserLdap());
    changeStatus.setStatus(status.toString());
    Response<PaymentTask> resp = paymentClient.updatePaymentTask(paymentTaskId, changeStatus);

    Assert.assertTrue(resp.isSuccessful(), "API: Payment update failed due to ERROR: " + resp.toString());
    Assert.assertEquals(status.toString(), resp.asJson().getTaskStatus(),
        "API: Payment update failed due to wrong STATUS: " + resp.toString());

    return resp;
  }

  private List<Link> getLinks(String paymentTaskId) {
    Response<PaymentTask> resp = paymentClient.getPaymentTask(paymentTaskId);

    Assert.assertTrue(resp.isSuccessful(), "API: GET Payment task failed due to ERROR: " + resp.toString());
    return resp.asJson().getLinks();
  }

  public String getShortPaymentLink(String orderId) {
    paymentTaskId = getPaymentTaskId(orderId);

    links = getLinks(paymentTaskId);
    return links.stream().filter(x -> x.getType().equals("SHORT_LINK")).findFirst().orElse(new Link()).getLink();
  }

  public String getPaymentLink(String orderId) {
    paymentTaskId = getPaymentTaskId(orderId);

    links = getLinks(paymentTaskId);
    return links.stream().filter(x -> x.getType().equals("LINK")).findFirst().orElse(new Link()).getLink();
  }

}
