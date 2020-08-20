package com.leroy.magportal.api.requests.payments;

import ru.leroymerlin.qa.core.clients.base.Method;
import ru.leroymerlin.qa.core.clients.base.RequestBuilder;
import ru.leroymerlin.qa.core.clients.puz.requests.CreateOrder;

@Method(value = "PUT", path = "/payment/v1/task/change-status")
public class PaymentRequest extends RequestBuilder<PaymentRequest> {

  public PaymentRequest setPaymentTaskId(String val) {
    return queryParam("paymentTaskId", val);
  }

  public PaymentRequest setStatus(String val) {
    return jsonBody("status", val);
  }

  public PaymentRequest setUpdatedBy(String val) {
    return jsonBody("updatedBy", val);
  }

  public PaymentRequest setContentType() { return header("Content-Type", "application/json"); }
}