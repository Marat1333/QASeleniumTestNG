package com.leroy.magmobile.models.search;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class FiltersData /*extends BaseModel*/ {

    private String filterFrame; // Мой магазин или Вся гамма ЛМ
    private String[] gamma;
    private String[] top;
    private boolean hasAvailableStock;
    private boolean topEM;
    private boolean bestPrice;
    private boolean top1000;
    private boolean limitedOffer;
    private boolean ctm;
    private String productType;
    private String[] supplier; // Возможно, лучше вместо String использовать соответствующий класс
    private boolean avs;
    private LocalDate dateAvs;

    public FiltersData(String filterFrame) {
        this.filterFrame = filterFrame;
    }

    public void setGamma(String gamma) {
        this.gamma = new String[]{gamma};
    }

    public void setGamma(String[] gamma) {
        this.gamma = gamma;
    }

    public void setTop(String top) {
        this.top = new String[]{top};
    }

    public void setTop(String[] top) {
        this.top = top;
    }

    public void setSupplier(String supplier) {
        this.supplier = new String[]{supplier};
    }

    public void setSupplier(String[] supplier) {
        this.supplier = supplier;
    }

    public void setDateAvs(LocalDate dateAvs) {
        this.avs = true;
        this.dateAvs = dateAvs;
    }

    public FiltersData unionWith(FiltersData newData) {
        setHasAvailableStock(this.isHasAvailableStock() && newData.isHasAvailableStock());
        setTopEM(this.isTopEM() && newData.isTopEM());
        setBestPrice(this.isBestPrice() && newData.isBestPrice());
        setTop1000(this.isTop1000() && newData.isTop1000());
        setLimitedOffer(this.isLimitedOffer() && newData.isLimitedOffer());
        setCtm(this.isCtm() && newData.isCtm());
        setAvs(this.isAvs() && newData.isAvs());
        if (productType == null)
            setProductType(newData.getProductType());
        if (dateAvs == null)
            setDateAvs(newData.getDateAvs());
        setGamma(unionStringArraysWithDistinct(this.getGamma(), newData.getGamma()));
        setTop(unionStringArraysWithDistinct(this.getTop(), newData.getTop()));
        setSupplier(unionStringArraysWithDistinct(this.getSupplier(), newData.getSupplier()));
        return this;
    }

    private String[] unionStringArraysWithDistinct(String[] first, String[] second) {
        Set<String> set;
        if (first != null)
            set = new HashSet<>(Arrays.asList(first));
        else
            set = new HashSet<>();
        if (second != null)
            set.addAll(Arrays.asList(second));
        return set.toArray(new String[0]);
    }

}
