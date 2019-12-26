package com.leroy.constants;

public enum MagMobElementTypes {

    EDIT_PEN("pen"),
    PLUS("plus"),
    CIRCLE_PLUS("circle_plus");

    private String pictureName;

    MagMobElementTypes(String pictureName) {
        this.pictureName = pictureName;
    }

    MagMobElementTypes() {
    }

    public String getPictureName() {
        return pictureName;
    }
}
