package com.leroy.constants;

public enum MagMobElementTypes {

    EDIT_PEN("pen"),
    PLUS("plus"),
    CIRCLE_PLUS("circle_plus"),
    CHECK_BOX_SELECTED("checkBoxSupplierSearchPage"),
    CHECK_BOX_FILTER_PAGE("checkBoxFilterPage"),
    OVAL_CHECKBOX("ovalCheckBoxWithText");

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
