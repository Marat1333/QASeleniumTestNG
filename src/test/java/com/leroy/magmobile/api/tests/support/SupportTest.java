package com.leroy.magmobile.api.tests.support;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;

import com.google.inject.Inject;
import com.leroy.magmobile.api.clients.SupportClient;
import com.leroy.magmobile.api.data.support.SupportTicketData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SupportTest extends BaseProjectApiTest {

    @Inject
    public SupportClient supportClient;

    @Test(description = "C3254683 PUT support/ticket")
    public void testPutSupportTicket() {
        SupportTicketData supportTicketData = new SupportTicketData();
        supportTicketData.generateRequiredReqData();
        Response<SupportTicketData> resp = supportClient.createSupportTicket(supportTicketData);
        isResponseOk(resp);
        assertThat("ticket number", resp.asJson().getTicketNumber(), not(emptyOrNullString()));
    }

}
