package com.leroy.constants;

public enum MagMobElementTypes {

    EditPen("pen"), Plus("plus");

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
