package com.leroy.umbrella_extension.authorization.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenData {
    private String access_token;
    private Integer expires_in;
    private String token_type;
    private String refresh_token;
    private String scope;
}
