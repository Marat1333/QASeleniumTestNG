package com.leroy.magmobile.api.data.ruptures;

import lombok.Data;

import java.util.List;

@Data
public class ReqRuptureSessionWithActionsData {
    private Integer sessionId;
    private String lmCode;
    private List<ActionData> actions;
}
