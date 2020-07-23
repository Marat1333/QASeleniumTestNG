package com.leroy.magmobile.ui.pages.work.print_tags.data;

import lombok.Data;

@Data
public class ProductTagData {
    private String lmCode;
    private String barCode;
    private String title;
    private boolean smallSize;
    private boolean middleSize;
    private boolean bigSize;
    private String smallSizeCount;
    private String middleSizeCount;
    private String bigSizeCount;
}
