package com.leroy.magportal.ui.models.search;

import com.leroy.magportal.ui.pages.products.SearchProductPage;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FiltersData {
    private SearchProductPage.Filters[] checkBoxes = new SearchProductPage.Filters[]{};
    private String[] gammaFilters = new String[]{};
    private String[] topFilters = new String[]{};
    private String[] suppliers = new String[]{};
    private LocalDate avsDate;

    public void clearFilterData() {
        gammaFilters = new String[]{};
        topFilters = new String[]{};
        suppliers = new String[]{};
        avsDate = null;
    }
}
