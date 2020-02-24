package com.leroy.magmobile.models.search;

import com.leroy.magmobile.models.CardWidgetData;
import com.leroy.umbrella_extension.magmobile.data.ServiceItemResponse;

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
        if (o instanceof ServiceItemResponse) {
            ServiceItemResponse serviceItemResponse = (ServiceItemResponse) o;
            return lmCode.equals(serviceItemResponse.getLmCode())&&name.equals(serviceItemResponse.getTitle());
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
