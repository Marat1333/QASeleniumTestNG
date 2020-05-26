package com.leroy.core.web_elements.general;

import com.leroy.core.fieldfactory.CustomLocator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class ComboBox extends BaseWidget {

    public ComboBox(WebDriver driver, CustomLocator locator) {
        super(driver, locator);
    }

    public void selectOptionByIndex(int index) {
        initialWebElementIfNeeded();
        List<WebElement> options = webElement.findElements(By.tagName("option"));
        options.get(index).click();
    }

    public List<String> getOptionsText() throws Exception {
        initialWebElementIfNeeded();
        List<String> optionsText = new ArrayList<>();
        List<WebElement> options = webElement.findElements(By.tagName("option"));
        for (WebElement option : options) {
            optionsText.add(option.getText());
        }
        return optionsText;
    }

    public void selectOptionByText(String text) {
        initialWebElementIfNeeded();
        Select dropDown = new Select(webElement);
        dropDown.selectByVisibleText(text);
    }

    public String getSelectedOptionText() {
        initialWebElementIfNeeded();
        String value = webElement.getAttribute("value");
        List<WebElement> options = webElement.findElements(By.tagName("option"));
        for (WebElement we : options) {
            if (value.equals(we.getAttribute("value"))) {
                return we.getText();
            }
        }
        return null;
    }

    public void selectOptionByValue(String option) {
        initialWebElementIfNeeded();
        List<WebElement> options = webElement.findElements(By.tagName("option"));
        for (WebElement optionElement : options) {
            if (option.equals(optionElement.getAttribute("value"))) {
                optionElement.click();
                break;
            }
        }
    }

    public int getCountOfOptions() {
        initialWebElementIfNeeded();
        List<WebElement> options = webElement.findElements(By.tagName("option"));
        return options.size();
    }

}
