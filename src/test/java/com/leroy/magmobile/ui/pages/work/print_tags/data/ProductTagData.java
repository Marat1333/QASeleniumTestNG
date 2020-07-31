package com.leroy.magmobile.ui.pages.work.print_tags.data;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class ProductTagData implements Serializable {
    private String lmCode;
    private String barCode;
    private String title;
    private int smallSizeCount;
    private int middleSizeCount;
    private int bigSizeCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductTagData)) return false;
        ProductTagData that = (ProductTagData) o;
        return smallSizeCount == that.smallSizeCount &&
                middleSizeCount == that.middleSizeCount &&
                bigSizeCount == that.bigSizeCount &&
                Objects.equals(lmCode, that.lmCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lmCode, smallSizeCount, middleSizeCount, bigSizeCount);
    }
}
