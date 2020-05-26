package com.leroy.magmobile.ui.constants;

public enum MagMobElementTypes {

    EDIT_PEN("pen"),
    SORT_RADIOGROUP_SORT_PAGE("sortRadioGroupSortPage"),
    PLUS("plus"),
    PLUS_FILTER_PAGE("plusFilterPage"),
    CROSS_FILTER_PAGE("crossFilterPage"),
    CIRCLE_PLUS("circle_plus"),
    CHECK_BOX_SELECTED_SUPPLIER_SEARCH_PAGE("checkBoxSupplierSearchPage"),
    CHECK_BOX_NOT_SELECTED_SUPPLIER_SEARCH_PAGE("checkBoxNotSelectedSupplierSearchPage"),
    CHECK_BOX_SELECTED_FILTER_PAGE("checkBoxFilterPage"),
    CHECK_BOX_NOT_SELECTED_FILTER_PAGE("checkBoxNotSelectedFilterPage");

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
