package com.leroy.magportal.api.helpers;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import com.google.inject.Inject;
import com.leroy.core.ContextProvider;
import com.leroy.core.configuration.DriverFactory;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.constants.CardConst;
import com.leroy.magportal.api.constants.PaymentMethodEnum;
import com.leroy.magportal.api.constants.PaymentStatusEnum;
import com.leroy.magportal.api.data.onlineOrders.OnlineOrderData;
import com.leroy.magportal.api.helpers.ui.PaymentPage;
import com.leroy.umbrella_extension.tpnet.TpNetClient;
import io.qameta.allure.Step;
import java.util.List;
import org.openqa.selenium.WebDriver;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.payment.PaymentClient;
import ru.leroymerlin.qa.core.clients.payment.data.task.ChangeStatus;
import ru.leroymerlin.qa.core.clients.payment.data.task.Link;
import ru.leroymerlin.qa.core.clients.payment.data.task.PaymentTask;

public class PaymentHelper extends BaseHelper {

    @Inject
    private PaymentClient paymentClient;
    @Inject
    OrderClient orderClient;
    @Inject
    TpNetClient tpNetClient;

    private String getPaymentTaskId(String orderId) {
        Response<OnlineOrderData> resp = orderClient.getOnlineOrder(orderId);
        if (!resp.isSuccessful()) {
            int tryCount = 3;
            for (int i = 0; i < tryCount; i++) {
                resp = orderClient.getOnlineOrder(orderId);
                if (resp.isSuccessful()) {
                    break;
                }
            }
        }
        assertThat("API: Impossible to get Order", resp, successful());
        return resp.asJson().getPaymentTaskId();
    }

    private void updatePayment(String orderId, PaymentStatusEnum status) {
        String paymentTaskId = getPaymentTaskId(orderId);
        /* TODO: Uses for Card Payment
        Response<PaymentTask> paymentTaskResponse = paymentClient.getPaymentTask(paymentTaskId);
        if (paymentTaskResponse.isSuccessful()
                && status.equals(PaymentStatusEnum.PAID)
                && paymentTaskResponse.asJson().getTaskType().equals("SBERLINK_WITH_TPNET_DEPOSIT")) {
            return;
        }
        */
        ChangeStatus changeStatus = new ChangeStatus();

        changeStatus.setUpdatedBy(userSessionData().getUserLdap());
        changeStatus.setStatus(status.toString());
        Response<PaymentTask> resp = paymentClient.updatePaymentTask(paymentTaskId, changeStatus);

        assertThat("Payment update failed", resp, successful());
        PaymentTask body = resp.asJson();
        assertThat("API: Payment update failed due to wrong STATUS: " + resp.toString(),
                status.toString(), equalTo(body.getTaskStatus()));
    }

    private List<Link> getLinks(String paymentTaskId) {
        Response<PaymentTask> resp = paymentClient.getPaymentTask(paymentTaskId);
        assertThat("GET Payment task failed ", resp, successful());
        return resp.asJson().getLinks();
    }

    private String getShortPaymentLink(String orderId) {
        String paymentTaskId = getPaymentTaskId(orderId);
        List<Link> links = getLinks(paymentTaskId);
        return links.stream().filter(x -> x.getType().equals("SHORT_LINK")).findFirst()
                .orElse(new Link()).getLink();
    }

    private String getPaymentLink(String orderId) {
        String paymentTaskId = getPaymentTaskId(orderId);
        List<Link> links = getLinks(paymentTaskId);
        return links.stream().filter(x -> x.getType().equals("LINK")).findFirst().orElse(new Link())
                .getLink();
    }

    private void holdCostByTpNet(String orderId) {
        tpNetClient.postTpNetPayment(orderId);
    }

    private void makeHoldCost(String orderId) {
        updatePayment(orderId, PaymentStatusEnum.HOLD);
    }

    private void makePaymentCard(String orderId) {

        try {
            WebDriver driver = DriverFactory.createDriver();
            ContextProvider.setDriver(driver);
            String link = getPaymentLink(orderId);
            driver.get(link);

            PaymentPage paymentPage = new PaymentPage();
            paymentPage.enterCreditCardDetails(CardConst.VISA_1111);
            paymentPage.assertThatPaymentIsSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ContextProvider.quitDriver();
        }
    }

    // Public methods
    @Step("Hold costs or Pay via selected payment type {orderId}")
    public void makePayment(String orderId, PaymentMethodEnum paymentType) {
        switch (paymentType) {
            case API:
                makeHoldCost(orderId);
                break;
            case CARD:
                makePaymentCard(orderId);
                break;
            case TPNET:
                holdCostByTpNet(orderId);
                break;
            default:
                break;
        }
    }

    @Step("PAID via API for Order with {orderId}")
    public void makePaid(String orderId) {
        updatePayment(orderId, PaymentStatusEnum.PAID);
    }
}
