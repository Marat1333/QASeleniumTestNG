package com.leroy.magmobile.ui.pages.work.supply_plan;

import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.RadioButton;
import com.leroy.magmobile.ui.pages.common.CommonMagMobilePage;

public class PeriodSelectorPage extends CommonMagMobilePage {

    public enum PeriodOption {
        YESTERDAY,
        TODAY,
        WEEK;
    }

    @AppFindBy(accessibilityId = "CloseModal")
    Button closeModalBtn;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text,'Вчера')]/following-sibling::android.view.ViewGroup")
    RadioButton yesterdayOption;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text,'Сегодня')]/following-sibling::android.view.ViewGroup")
    RadioButton todayOption;

    @AppFindBy(xpath = "//android.widget.TextView[contains(@text,'Неделя')]/following-sibling::android.view.ViewGroup")
    RadioButton weekOption;

    public SuppliesListPage selectPeriodOption(PeriodOption option) {
        switch (option) {
            case YESTERDAY:
                yesterdayOption.click();
                break;
            case TODAY:
                todayOption.click();
                break;
            case WEEK:
                weekOption.click();
                break;
        }
        return new SuppliesListPage();
    }
}
