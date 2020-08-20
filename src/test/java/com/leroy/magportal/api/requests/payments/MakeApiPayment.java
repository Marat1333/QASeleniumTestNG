package com.leroy.magportal.api.requests.payments;

import com.google.inject.Inject;
import com.leroy.magportal.api.helpers.PaymentHelper;
import com.leroy.magportal.api.tests.cart.BaseMagPortalApiTest;
import org.testng.annotations.Test;

public class MakeApiPayment extends BaseMagPortalApiTest {

  @Inject
  private PaymentHelper paymentHelper;

  public void makeHoldCost(String orderId) {
    paymentHelper.updatePayment(orderId, PaymentStatusEnum.HOLD);
  }

  public void makePaid(String orderId) {
    paymentHelper.updatePayment(orderId, PaymentStatusEnum.PAID);
  }
}
