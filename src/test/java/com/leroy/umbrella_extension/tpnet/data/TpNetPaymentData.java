package com.leroy.umbrella_extension.tpnet.data;

import lombok.Data;

@Data
public class TpNetPaymentData {

    private String vhost;
    private String name;
    private Properties properties;
    private String routing_key;
    private int delivery_mode;
    private String payload;
    private String payload_encoding;
    private Object headers;
    private Object props;

    @Data
    public static class Properties {

        private int delivery_mode;
        private Object headers;
    }
}
