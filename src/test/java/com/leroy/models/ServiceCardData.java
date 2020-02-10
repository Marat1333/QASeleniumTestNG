package com.leroy.models;

import com.leroy.umbrella_extension.magmobile.data.ProductItemResponse;

public class ServiceCardData extends CardWidgetData {

    private String lmCode;
    private String name;

    public String getLmCode() {
        return lmCode;
    }

    public void setLmCode(String lmCode) {
        this.lmCode = lmCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ProductItemResponse) {
            ProductItemResponse productItemResponse = (ProductItemResponse) o;
            return lmCode.equals(productItemResponse.getLmCode());
        }
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        } else
            return super.equals(o);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + lmCode.hashCode();
        result = prime * result + name.hashCode();
        return result;
    }
}
