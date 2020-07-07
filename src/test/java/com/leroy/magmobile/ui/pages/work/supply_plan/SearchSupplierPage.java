package com.leroy.magmobile.ui.pages.work.supply_plan;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.android.AndroidScrollView;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.EditBox;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;
import com.leroy.magmobile.ui.pages.work.supply_plan.data.SearchHistoryElementData;
import com.leroy.magmobile.ui.pages.work.supply_plan.widgets.SearchHistoryElementWidget;

public class SearchSupplierPage extends CommonMagMobilePage {

    @AppFindBy(accessibilityId = "BackButton")
    Button backBtn;

    @AppFindBy(accessibilityId = "ScreenTitle-SuppliesSearch")
    EditBox searchInput;

    @AppFindBy(accessibilityId = "Button")
    Button clearSearchInput;

    @AppFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"ScreenContent\"]/android.view.ViewGroup[2]//android.widget.TextView")
    Button selectDepartmentBtn;


    AndroidScrollView<SearchHistoryElementData> searchHistory = new AndroidScrollView<>(driver,
            AndroidScrollView.TYPICAL_LOCATOR, "./android.view.ViewGroup/android.view.ViewGroup/android.view.ViewGroup",
            SearchHistoryElementWidget.class);

}
