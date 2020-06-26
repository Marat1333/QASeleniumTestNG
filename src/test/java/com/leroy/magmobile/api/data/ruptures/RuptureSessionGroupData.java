package com.leroy.magmobile.api.data.ruptures;

import lombok.Data;

@Data
public class RuptureSessionGroupData {
    private Integer action;
    private Integer activeCount;
    private Integer finishedCount;

    public void increaseActiveCount() {
        if (activeCount == null)
            activeCount = 1;
        else
            activeCount++;
    }

    public void increaseFinishedCount() {
        if (finishedCount == null)
            finishedCount = 1;
        else
            finishedCount++;
    }
}
