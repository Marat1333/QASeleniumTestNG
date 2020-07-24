package com.leroy.magmobile.ui.pages.work.print_tags.data;

import lombok.Data;

@Data
public class ProductTagData {
    private String lmCode;
    private String barCode;
    private String title;
    private int smallSizeCount;
    private int middleSizeCount;
    private int bigSizeCount;
}
