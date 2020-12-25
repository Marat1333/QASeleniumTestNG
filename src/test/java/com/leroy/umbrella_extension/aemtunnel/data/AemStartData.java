package com.leroy.umbrella_extension.aemtunnel.data;

import lombok.Data;
import ru.leroymerlin.qa.core.clients.tunnel.data.StepStartPayload;

@Data
public class AemStartData extends StepStartPayload {

    @Data
    public class Product {

        private String productId;
        private Double quantity;
    }
}
