package com.leroy.magmobile.api.clients;

import com.leroy.magmobile.api.data.support.SupportTicketData;
import com.leroy.magmobile.api.requests.support.SupportTicketRequest;
import io.qameta.allure.Step;
import java.nio.file.Path;
import lombok.SneakyThrows;
import ru.leroymerlin.qa.core.clients.base.Response;

public class SupportClient extends BaseMagMobileClient {

    /**
     * ---------- Executable Requests -------------
     **/

    @Step("Create support ticket")
    public Response<SupportTicketData> createSupportTicket(SupportTicketData supportTicketData) {
        return execute(new SupportTicketRequest().jsonBody(supportTicketData),
                SupportTicketData.class);
    }

    //TODO Not working until QA Core issue not fixed. + Need to extend with attach several files and IOException processing.
    @SneakyThrows
    @Step("Create support ticket with attachment")
    public Response<SupportTicketData> createSupportTicket(SupportTicketData supportTicketData, Path path) {
        SupportTicketRequest req = new SupportTicketRequest();
        req.jsonBody(supportTicketData);
        req.fileBody(path);
        return execute(req, SupportTicketData.class);
    }
}
