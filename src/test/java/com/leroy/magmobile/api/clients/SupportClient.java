package com.leroy.magmobile.api.clients;

import com.leroy.magmobile.api.data.support.SupportTicketData;
import com.leroy.magmobile.api.requests.support.SupportTicketRequest;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SupportClient extends MagMobileClient {

    /**
     * ---------- Executable Requests -------------
     **/

    public Response<SupportTicketData> createSupportTicket(SupportTicketData supportTicketData) {
        return execute(new SupportTicketRequest().jsonBody(supportTicketData),
                SupportTicketData.class);
    }

}
