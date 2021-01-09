package com.leroy.umbrella_extension.aemtunnel.data;

import lombok.Data;
import ru.leroymerlin.qa.core.clients.tunnel.data.PutPaymentResponse;

@Data
public class AemPaymentResponseData extends PutPaymentResponse {

    private String solutionId;
}
