package com.leroy.umbrella_extension.tpnet;


import com.google.inject.Inject;
import com.leroy.constants.EnvConstants;
import com.leroy.magmobile.api.data.sales.orders.OrderProductData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.data.onlineOrders.OnlineOrderData;
import com.leroy.umbrella_extension.tpnet.data.TpNetPaymentData;
import com.leroy.umbrella_extension.tpnet.data.TpNetPaymentData.Properties;
import com.leroy.umbrella_extension.tpnet.request.RabbitTpNetPostRequest;
import io.qameta.allure.Step;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.leroymerlin.qa.core.clients.base.BaseClient;
import ru.leroymerlin.qa.core.clients.base.Response;

public class TpNetClient extends BaseClient {

    @Inject
    private OrderClient orderClient;

    protected String gatewayUrl;
    private final String login = EnvConstants.RABBIT_USER_NAME;
    private final String password = EnvConstants.RABBIT_USER_PASS;
    private Document document;

    @Step("Send request to TpNet via Rabbit")
    public Response<?> postTpNetPayment(String orderId) {
        RabbitTpNetPostRequest req = new RabbitTpNetPostRequest();
        req.basicAuthHeader(login, password);
        req.jsonBody(makeBody(orderId));
        return execute(req.build(gatewayUrl), Object.class);
    }

    private TpNetPaymentData makeBody(String orderId) {
        TpNetPaymentData paymentData = new TpNetPaymentData();
        Properties properties = new Properties();
        properties.setDelivery_mode(2);
        properties.setHeaders(new Object());
        OnlineOrderData orderData = orderClient.getOnlineOrder(orderId).asJson();
        paymentData.setVhost("tpnet");
        paymentData.setName("brokerExchange");
        paymentData.setProperties(properties);
        paymentData.setRouting_key("DEPOSITE-IN");
        paymentData.setHeaders(new Object());
        paymentData.setDelivery_mode(2);
        paymentData.setProps(new Object());
        paymentData.setPayload_encoding("string");
        paymentData.setPayload(convertXMLDocumentToString(makeXml(orderData)));
        return paymentData;
    }

    private Document makeXml(OnlineOrderData orderData) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.newDocument();

            Element rootElement = document.createElement("customerOrderOrderDeposit");
            rootElement
                    .setAttribute("xmlns", "http://www.adeo.com/diamant/customerOrderOrderDeposit");
            rootElement.appendChild(createNode("businessUnitCode", "009"));
            rootElement.appendChild(createNode("storeCode", orderData.getShopId()));

            Element orderDeposits = document.createElement("orderDeposits");
            Element orderDeposit = document.createElement("orderDeposit");
            orderDeposit.appendChild(createNode("customerOrderCode", orderData.getOrderId()));
            orderDeposit.appendChild(createNode("valueDue", "400"));
            orderDeposit.appendChild(createNode("isManual", "0"));
            Element lines = document.createElement("lines");
            for (OrderProductData productData : orderData.getProducts()) {
                lines.appendChild(createLine(productData));
            }
            orderDeposit.appendChild(lines);
            orderDeposits.appendChild(orderDeposit);
            rootElement.appendChild(orderDeposits);

            Element receiptData = document.createElement("receiptData");
            receiptData.appendChild(createNode("receiptNumber", "7"));
            receiptData.appendChild(createNode("receiptCashpointNumber", "99"));
            receiptData.appendChild(createNode("receiptDate",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            rootElement.appendChild(receiptData);

            document.appendChild(rootElement);
            return document;
        } catch (Exception ex) {
            throw new RuntimeException("Error creating XML", ex);
        }
    }

    private Element createNode(String name, String value) {
        try {
            Element element = document.createElement(name);
            element.appendChild(document.createTextNode(value));
            return element;
        } catch (Exception ex) {
            throw new RuntimeException("Error creating XML", ex);
        }
    }

    private Element createLine(OrderProductData productData) {
        try {
            Element element = document.createElement("line");
            element.setAttribute("lineId", productData.getLineId());
            element.setAttribute("productCode", productData.getLmCode());
            element.setAttribute("quantity",
                    productData.getQuantity().toString());//TODO: ?confirmed quantity
            element.setAttribute("price", productData.getPrice().toString());
            return element;
        } catch (Exception ex) {
            throw new RuntimeException("Error creating XML", ex);
        }
    }

    private static String convertXMLDocumentToString(Document document) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(document), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }

    @PostConstruct
    private void init() {
        gatewayUrl  = EnvConstants.RABBIT_API_HOST;
    }
}
