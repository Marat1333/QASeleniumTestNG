package com.leroy.magportal.ui.pages.cart_estimate;

import com.leroy.constants.EnvConstants;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.pages.BaseWebPage;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.models.salesdoc.EstimatePrintProductData;
import com.leroy.magportal.ui.models.salesdoc.ProductOrderCardWebData;
import com.leroy.magportal.ui.models.salesdoc.SalesDocWebData;
import com.leroy.magportal.ui.pages.cart_estimate.widget.EstimatePrintProductRowWidget;
import com.leroy.magportal.ui.webelements.CardWebWidgetList;
import com.leroy.utils.DateTimeUtil;
import com.leroy.utils.ParserUtil;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class PrintEstimatePage extends BaseWebPage {

    private String printEstimateHandle;

    public PrintEstimatePage(String handlePrintEstimatePage) {
        super();
        this.printEstimateHandle = handlePrintEstimatePage;
    }

    private WebElement getShadowRootElement(WebElement element) {
        return (WebElement) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot", element);
    }

    private WebElement printPreviewSideBar() {
        return getShadowRootElement(driver.findElement(By.xpath("//print-preview-app")))
                .findElement(By.cssSelector("print-preview-sidebar[id=sidebar]"));
    }

    private WebElement printPreviewButtons() {
        return getShadowRootElement(printPreviewSideBar())
                .findElement(By.cssSelector("print-preview-button-strip"));
    }

    private WebElement printPreviewCancelBtn() {
        return getShadowRootElement(printPreviewButtons())
                .findElement(By.cssSelector("cr-button[class='cancel-button']"));
    }

    private WebElement printPreviewPrintBtn() {
        return getShadowRootElement(printPreviewButtons())
                .findElement(By.cssSelector("cr-button[class='action-button']"));
    }

    @WebFindBy(xpath = "//p", metaName = "Первый и основной загаловок")
    Element header;

    @WebFindBy(containsText = "была оформлена на сумму", metaName = "Краткое описание (подзаголовок)")
    Element description;

    @WebFindBy(containsText = "Смета действительна до:")
    Element estimateExpiredDate;

    @WebFindBy(containsText = "Бланк оформил:")
    Element seller;

    @WebFindBy(containsText = "Адрес магазина:")
    Element shopAddress;

    @WebFindBy(xpath = "//div[contains(@class, 'PrintLayout-screens__block')]/span[2]", metaName = "Номер сметы")
    Element estimateNumber;

    @WebFindBy(xpath = "//div[@class='lmui-View']/div[contains(@class, 'EstimatePrint__product')]",
            clazz = EstimatePrintProductRowWidget.class)
    CardWebWidgetList<EstimatePrintProductRowWidget, EstimatePrintProductData> products;

    private final static String SUMMARY_INFO_AREA_XPATH = "//div[contains(@class, 'PrintLayout-screens__summary')]";

    @WebFindBy(xpath = SUMMARY_INFO_AREA_XPATH + "/span[1]")
    Element totalPriceWithoutNDS;

    @WebFindBy(xpath = SUMMARY_INFO_AREA_XPATH + "/span[2]")
    Element amountNDS;

    @WebFindBy(xpath = SUMMARY_INFO_AREA_XPATH + "/span[3]")
    Element totalPriceWithNDS;

    private final static String BOTTOM_BLOCK_XPATH = "(//div[contains(@class, 'PrintLayout-screens__block')])[last()]";

    @WebFindBy(xpath = BOTTOM_BLOCK_XPATH + "/div/span[2]")
    Element recipientName;

    @WebFindBy(xpath = BOTTOM_BLOCK_XPATH + "/div[2]/div[2]/span")
    Element recipientPhone;

    // Actions
    @Step("Нажмите 'Отмена'")
    public PrintEstimatePage clickCancelButton() throws Exception {
        printPreviewCancelBtn().click();
        switchToWindow(printEstimateHandle);
        return this;
    }

    // Verifications

    @Step("Проверить, что модальное окно предварительного просмотра печати отображается корректно")
    public PrintEstimatePage shouldPrintPreviewAreVisible() {
        anAssert.isTrue(printPreviewButtons().isDisplayed(),
                "Окно предварительного просмотра печати не отображается");
        softAssert.isTrue(printPreviewCancelBtn().isDisplayed(), "Кнопка 'Cancel' не отображается");
        softAssert.isTrue(printPreviewPrintBtn().isDisplayed(), "Кнопка 'Print' не отображается");
        softAssert.verifyAll();
        return this;
    }

    @Step("Проверить, что текст для печати соответствуют данным, которые были в смете")
    public PrintEstimatePage shouldEstimatePrintDataIs(SalesDocWebData estimateData) throws Exception {
        softAssert.isContainsIgnoringCase(header.getText(), estimateData.getClient().getName(),
                "В загаловке должно присутствовать имя клиента");
        String descriptionText = description.getText();
        softAssert.isContainsIgnoringCase(descriptionText, estimateData.getNumber(),
                "В описании сметы должен быть номер сметы");
        softAssert.isContainsIgnoringCase(descriptionText.replaceAll(" ", ""), ParserUtil.doubleToStr(
                estimateData.getOrders().get(0).getTotalPrice(), 2, true),
                "В описании сметы должна быть итоговая сумма");
        softAssert.isContainsIgnoringCase(estimateExpiredDate.getText(),
                DateTimeUtil.localDateToStr(LocalDate.now().plusDays(14), "dd.MM.yy"),
                "Неверная дата, до которой действительна смета");
        softAssert.isContainsIgnoringCase(seller.getText(), EnvConstants.BASIC_USER_FIRST_NAME,
                "Неверное имя в поле 'Бланк оформил'");
        softAssert.isElementVisible(shopAddress);
        // Состав сметы
        softAssert.isEquals(estimateNumber.getText(), estimateData.getNumber(), "Неверный номер сметы");
        List<EstimatePrintProductData> actualProductDataList = products.getDataList();
        List<ProductOrderCardWebData> expectedProductList = estimateData.getOrders().get(0).getProductCardDataList();
        anAssert.isEquals(actualProductDataList.size(), expectedProductList.size(),
                "Ожидалось другое кол-во товаров");
        for (int i = 0; i < actualProductDataList.size(); i++) {
            EstimatePrintProductData actualProduct = actualProductDataList.get(i);
            ProductOrderCardWebData expectedProduct = expectedProductList.get(i);

            softAssert.isEquals(actualProduct.getLmCode(), expectedProduct.getLmCode(),
                    "Товар " + (i + 1) + " - неверный ЛМ код");
            softAssert.isEquals(actualProduct.getTitle(), expectedProduct.getTitle().isEmpty() ? "-" : expectedProduct.getTitle(),
                    "Товар " + (i + 1) + " - неверное название");
            softAssert.isEquals(actualProduct.getQuantity(), expectedProduct.getSelectedQuantity(),
                    "Товар " + (i + 1) + " - неверное кол-во");
            softAssert.isEquals(actualProduct.getPercentNDS(), 20.0,
                    "Товар " + (i + 1) + " - неверный НДС");
            softAssert.isEquals(actualProduct.getPrice(), expectedProduct.getPrice(),
                    "Товар " + (i + 1) + " - неверная цена");
            softAssert.isEquals(actualProduct.getTotalPriceWithNDS(),
                    ParserUtil.multiply(actualProduct.getQuantity(), actualProduct.getPrice(), 2),
                    "Товар " + (i + 1) + " - неверная сумма с учетом НДС");
        }

        // Итого суммы:
        double expectedTotalPriceWithNDS = estimateData.getOrders().get(0).getTotalPrice();
        double expectedTotalPriceWithoutNDS = new BigDecimal(expectedTotalPriceWithNDS / 1.2)
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
        softAssert.isEquals(ParserUtil.strToDouble(totalPriceWithoutNDS.getText()), expectedTotalPriceWithoutNDS,
                "Неверная итого без НДС");
        softAssert.isEquals(ParserUtil.strToDouble(StringUtils.substringAfter(amountNDS.getText(), "):")),
                ParserUtil.minus(expectedTotalPriceWithNDS, expectedTotalPriceWithoutNDS, 2),
                "Неверная сумма НДС");
        softAssert.isEquals(ParserUtil.strToDouble(totalPriceWithNDS.getText()), expectedTotalPriceWithNDS,
                "Неверная стоимость с НДС");

        // Нижняя часть - получатель
        softAssert.isEquals(recipientName.getText(), estimateData.getClient().getName(),
                "Неверное имя получателя");
        softAssert.isEquals(recipientPhone.getText(),
                estimateData.getClient().getPhoneNumber().replaceAll("\\+", ""),
                "Неверное номер телефона получателя");
        softAssert.verifyAll();
        return this;
    }

}
