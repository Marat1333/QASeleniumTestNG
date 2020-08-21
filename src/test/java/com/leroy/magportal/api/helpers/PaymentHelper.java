package com.leroy.magportal.api.helpers;

import com.google.inject.Inject;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.constants.PaymentStatusEnum;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.payment.PaymentClient;
import ru.leroymerlin.qa.core.clients.payment.data.task.ChangeStatus;
import ru.leroymerlin.qa.core.clients.payment.data.task.Link;
import ru.leroymerlin.qa.core.clients.payment.data.task.PaymentTask;

import java.util.List;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class PaymentHelper extends BaseHelper {

    @Inject
    private PaymentClient paymentClient;

    private String getPaymentTaskId(String orderId) {
        OrderClient orderClient = getOrderClient();
        Response<OrderData> resp = orderClient.getOrder(orderId);
        if (!resp.isSuccessful()) {
            int tryCount = 3;
            for (int i = 0; i < tryCount; i++) {
                resp = orderClient.getOrder(orderId);
                if (resp.isSuccessful())
                    break;
            }
        }
        assertThat("API: Impossible to get Order", resp, successful());
        return resp.asJson().getPaymentTaskId();
    }

    private PaymentTask updatePayment(String orderId, PaymentStatusEnum status) {
        String paymentTaskId = getPaymentTaskId(orderId);
        ChangeStatus changeStatus = new ChangeStatus();

        changeStatus.setUpdatedBy(userSessionData().getUserLdap());
        changeStatus.setStatus(status.toString());
        Response<PaymentTask> resp = paymentClient.updatePaymentTask(paymentTaskId, changeStatus);

        assertThat("Payment update failed", resp, successful());
        PaymentTask body = resp.asJson();
        assertThat("API: Payment update failed due to wrong STATUS: " + resp.toString(),
                status.toString(), equalTo(body.getTaskStatus()));

        return body;
    }

    private List<Link> getLinks(String paymentTaskId) {
        Response<PaymentTask> resp = paymentClient.getPaymentTask(paymentTaskId);
        assertThat("GET Payment task failed ", resp, successful());
        return resp.asJson().getLinks();
    }

    // Public methods

    public String getShortPaymentLink(String orderId) {
        String paymentTaskId = getPaymentTaskId(orderId);
        List<Link> links = getLinks(paymentTaskId);
        return links.stream().filter(x -> x.getType().equals("SHORT_LINK")).findFirst().orElse(new Link()).getLink();
    }

    public String getPaymentLink(String orderId) {
        String paymentTaskId = getPaymentTaskId(orderId);
        List<Link> links = getLinks(paymentTaskId);
        return links.stream().filter(x -> x.getType().equals("LINK")).findFirst().orElse(new Link()).getLink();
    }

    public void makeHoldCost(String orderId) {
        updatePayment(orderId, PaymentStatusEnum.HOLD);
    }

    public void makePaid(String orderId) {
        updatePayment(orderId, PaymentStatusEnum.PAID);
    }

}
