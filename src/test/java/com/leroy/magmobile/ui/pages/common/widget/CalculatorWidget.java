package com.leroy.magmobile.ui.pages.common.widget;

import com.leroy.core.ContextProvider;
import com.leroy.core.annotations.AppFindBy;
import com.leroy.core.web_elements.general.BaseWidget;
import com.leroy.core.web_elements.general.Element;

public class CalculatorWidget extends BaseWidget {

    public CalculatorWidget() {
        super(ContextProvider.getDriver());
        initElements();
    }

    @AppFindBy(xpath = "//*[@content-desc='Button' and android.widget.TextView[@text='-']]", metaName = "'-'")
    Element minus;

    @AppFindBy(xpath = "//*[@content-desc='Button' and android.widget.TextView[@text='+']]", metaName = "'+'")
    Element plus;

    @AppFindBy(xpath = "//*[@content-desc='Button' and android.widget.TextView[@text='×']]", metaName = "'×'")
    Element multiply;

    @AppFindBy(xpath = "//*[@content-desc='Button' and android.widget.TextView[@text='÷']]", metaName = "'÷'")
    Element division;

    @AppFindBy(xpath = "//*[@content-desc='Button' and android.widget.TextView[@text='=']]", metaName = "'='")
    Element equal;

    public void clickOperation(String value) {
        switch (value) {
            case "+":
                plus.click();
                break;
            case "-":
                minus.click();
                break;
            case "*":
                multiply.click();
                break;
            case "/":
                division.click();
                break;
            case "=":
                equal.click();
                break;
            default:
                throw new IllegalArgumentException("Недопустимая операция:" + value + " Допустимые значения: +, -, *, /, =");
        }
    }
}
