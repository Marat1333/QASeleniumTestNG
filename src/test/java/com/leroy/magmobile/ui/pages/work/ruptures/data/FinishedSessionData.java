package com.leroy.magmobile.ui.pages.work.ruptures.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class FinishedSessionData extends SessionData {
    private int finishedTaskQuantity;
    private int createdTaskQuantity;
}
