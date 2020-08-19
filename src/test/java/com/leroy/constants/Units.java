package com.leroy.constants;

public enum Units {
    EA("шт.", "EA");

    Units(String ruName, String enName) {
        this.ruName = ruName;
        this.enName = enName;
    }

    private String ruName, enName;

    public String getRuName() {
        return ruName;
    }

    public String getEnName() {
        return enName;
    }
}
