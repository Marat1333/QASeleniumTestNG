package com.leroy.core.pages;

import com.leroy.core.BaseContainer;
import org.openqa.selenium.WebDriver;

public class BaseAppPage extends BaseContainer {

    public BaseAppPage(WebDriver driver) {
        super(driver);
        initElements();
    }

}
