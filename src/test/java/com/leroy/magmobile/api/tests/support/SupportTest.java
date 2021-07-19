package com.leroy.magmobile.api.tests.support;

import com.google.inject.Inject;
import com.leroy.magmobile.api.clients.SupportClient;
import com.leroy.magmobile.api.data.support.SupportTicketData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

public class SupportTest extends BaseProjectApiTest {

    @Inject
    private SupportClient supportClient;

    @Test(description = "C3254683 PUT support/ticket")
    @TmsLink("3458")
    public void testPutSupportTicket() {
        SupportTicketData supportTicketData = new SupportTicketData();
        supportTicketData.generateRequiredReqData();
        Response<SupportTicketData> resp = supportClient.createSupportTicket(supportTicketData);
        isResponseOk(resp);
        assertThat("ticket number", resp.asJson().getTicketNumber(), not(emptyOrNullString()));
    }

}
