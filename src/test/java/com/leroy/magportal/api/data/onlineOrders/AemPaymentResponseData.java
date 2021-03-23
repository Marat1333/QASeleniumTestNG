package com.leroy.magportal.api.data.onlineOrders;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.leroymerlin.qa.core.clients.tunnel.data.PutPaymentResponse;

@EqualsAndHashCode(callSuper = true)
@Data
public class AemPaymentResponseData extends PutPaymentResponse {

    private String solutionId;
}
