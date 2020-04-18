package com.leroy.magmobile.api.tests.support;

import com.leroy.magmobile.api.clients.SupportClient;
import com.leroy.magmobile.api.data.support.SupportTicketData;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SupportTest extends BaseProjectApiTest {

    public SupportClient client() {
        return apiClientProvider.getSupportClient();
    }

    @Test(description = "C3254683 PUT support/ticket")
    public void testPutSupportTicket() {
        SupportTicketData supportTicketData = new SupportTicketData();
        supportTicketData.generateRequiredReqData();
        Response<SupportTicketData> resp = client().createSupportTicket(supportTicketData);
        isResponseOk(resp);
        assertThat("ticket number", resp.asJson().getTicketNumber(), not(emptyOrNullString()));
    }

}
