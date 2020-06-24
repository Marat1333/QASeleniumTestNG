package com.leroy.magmobile.api.data.ruptures;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Random;

@Data
public class ActionData {
    private Integer action;
    private Boolean state;
    private Integer userPosition;

    @JsonIgnore
    public void generateRandomData() {
        this.action = new Random().nextInt(6);
        this.state = new Random().nextBoolean();
        this.userPosition = 0;
    }

    public static ActionData returnRandomData() {
        ActionData data = new ActionData();
        data.generateRandomData();
        return data;
    }
}
