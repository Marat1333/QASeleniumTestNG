package com.leroy.magmobile.api.helpers;


import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

import com.google.inject.Inject;
import com.leroy.constants.sales.SalesDocumentsConst;
import com.leroy.core.configuration.Log;
import com.leroy.magmobile.api.clients.TransferClient;
import com.leroy.magmobile.api.data.sales.transfer.TransferProductOrderData;
import com.leroy.magmobile.api.data.sales.transfer.TransferRunRespData;
import com.leroy.magmobile.api.data.sales.transfer.TransferSalesDocData;
import com.leroy.magmobile.api.data.sales.transfer.TransferSearchProductData;
import com.leroy.magmobile.api.data.sales.transfer.TransferSearchProductDataList;
import com.leroy.magportal.api.helpers.BaseHelper;
import io.qameta.allure.Step;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import lombok.Data;
import lombok.experimental.Accessors;
import org.testng.Assert;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.fulfillment.data.internaltransfer.FulfillmentInternalTransferClient;
import ru.leroymerlin.qa.core.clients.fulfillment.data.internaltransfer.SimulateTaskResponse;

public class TransferHelper extends BaseHelper {

    @Inject
    FulfillmentInternalTransferClient fulfillmentClient;

    @Inject
    TransferClient transferClient;

    // ----------- SOAP --------------- //

    private static class TrustAllHosts implements HostnameVerifier {

        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static class TrustAllCertificates implements X509TrustManager {

        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    private void callSoapWebService(String soapEndpointUrl, String taskId, String extTaskId) {
        try {
            final boolean isHttps = soapEndpointUrl.toLowerCase().startsWith("https");
            HttpsURLConnection httpsConnection = null;
            // Open HTTPS connection
            if (isHttps) {
                // Create SSL context and trust all certificates
                SSLContext sslContext = SSLContext.getInstance("SSL");
                TrustManager[] trustAll
                        = new TrustManager[]{new TrustAllCertificates()};
                sslContext.init(null, trustAll, new java.security.SecureRandom());
                // Set trust all certificates context to HttpsURLConnection
                HttpsURLConnection
                        .setDefaultSSLSocketFactory(sslContext.getSocketFactory());
                // Open HTTPS connection
                URL url = new URL(soapEndpointUrl);
                httpsConnection = (HttpsURLConnection) url.openConnection();
                // Trust all hosts
                httpsConnection.setHostnameVerifier(new TrustAllHosts());
                // Connect
                httpsConnection.connect();
            }
            // Create SOAP Connection
            SOAPConnection soapConnection = SOAPConnectionFactory.newInstance().createConnection();

            // Send SOAP Message to SOAP Server
            SOAPMessage soapResponse = soapConnection
                    .call(createSOAPRequest(taskId, extTaskId), soapEndpointUrl);
            soapConnection.close();

            // Close HTTPS connection
            if (isHttps) {
                httpsConnection.disconnect();
            }
            Log.info("???????????? ???? ?????????? ???" + taskId + " ????????????????");
        } catch (Exception e) {
            Log.error(e.getMessage());
            Assert.fail("???? ?????????????? ???????????????? ???????????? ???? ?????????? id=" + taskId);
        }
    }

    private SOAPMessage createSOAPRequest(String taskId, String extTaskId) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        SOAPPart soapPart = soapMessage.getSOAPPart();

        String myNamespace1 = "api3";
        String myNamespace2 = "api31";
        String api3 = "urn:microsoft-dynamics-schemas/codeunit/API3";
        String api31 = "urn:microsoft-dynamics-nav/xmlports/API3_DocumentAction";
        String soapAction = "urn:microsoft-dynamics-schemas/codeunit/API3:CancelTasks";

        // SOAP Envelope
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration(myNamespace1, api3);
        envelope.addNamespaceDeclaration(myNamespace2, api31);

        // SOAP Body
        SOAPBody soapBody = envelope.getBody();

        SOAPElement cancelTasks = soapBody.addChildElement("CancelTasks", myNamespace1);
        SOAPElement request = cancelTasks.addChildElement("request", myNamespace1);
        SOAPElement responce = cancelTasks.addChildElement("responce", myNamespace1);

        SOAPElement tasks = request.addChildElement("Tasks", myNamespace2);

        SOAPElement buID = tasks.addChildElement("buID", myNamespace2);
        buID.addTextNode("9");
        SOAPElement storeID = tasks.addChildElement("storeID", myNamespace2);
        if (!userSessionData().getUserShopId().equals("35")) {
            throw new IllegalArgumentException(
                    "???????????????? ???????????? ???? ?????????? ???????????????? ???????????? ?????? 35 ????????????????");
        }
        storeID.addTextNode(userSessionData().getUserShopId());
        SOAPElement solutionID = tasks.addChildElement("solutionID", myNamespace2);
        solutionID.addTextNode(taskId);
        SOAPElement task = tasks.addChildElement("Task", myNamespace2);
        SOAPElement taskID = task.addChildElement("TaskID", myNamespace2);
        taskID.addTextNode(extTaskId);

        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", soapAction);
        // ?????????????????????? ?????????????????????? ??????????????
        String login = "WMSAPI";
        String password = "YdzWChVy8a7N0gF";
        String loginPassword = login + ":" + password;
        byte[] bytes = loginPassword.getBytes();
        String auth = new String(Base64.getMimeEncoder().encode(bytes));
        headers.addHeader("Authorization", "Basic " + auth);

        soapMessage.saveChanges();

        return soapMessage;
    }

    // fullfilment

    private String getExtTaskByTaskId(String taskId) {
        Response<SimulateTaskResponse> resp = fulfillmentClient.getInternalTransferTask(taskId);
        assertThat("GET http://fulfillment/v1/internal-transfer/task/" + taskId, resp,
                successful());
        return resp.asJson().getExtTaskId();
    }

    // ------ Public methods ---------- //

    @Step("API: ?????????????? ???????????? ???? ??????????")
    public TransferSalesDocData createDraftTransferTask(
            List<TransferProductOrderData> productDataList,
            SalesDocumentsConst.GiveAwayPoints giveAwayPoints) {
        TransferSalesDocData postSalesDocData = new TransferSalesDocData();
        postSalesDocData.setProducts(productDataList);
        postSalesDocData.setShopId(Integer.valueOf(userSessionData().getUserShopId()));
        postSalesDocData.setDepartmentId(userSessionData().getUserDepartmentId());
        postSalesDocData.setDateOfGiveAway(ZonedDateTime.now());
        postSalesDocData.setPointOfGiveAway(giveAwayPoints.getApiVal());

        // Send request
        Response<TransferSalesDocData> resp = transferClient.sendRequestCreate(postSalesDocData);
        assertThat(resp, successful());
        return resp.asJson();
    }

    @Step("API: ?????????????? Draft ???????????? ???? ??????????")
    public TransferSalesDocData createDraftTransferTask(
            TransferProductOrderData productData,
            SalesDocumentsConst.GiveAwayPoints giveAwayPoints) {
        return createDraftTransferTask(Collections.singletonList(productData), giveAwayPoints);
    }

    @Step("API: ?????????????? ???????????????????????????? ???????????? ???? ??????????")
    public TransferRunRespData createConfirmedTransferTask(
            List<TransferProductOrderData> productDataList,
            SalesDocumentsConst.GiveAwayPoints giveAwayPoints) {
        TransferSalesDocData transferSalesDocData = createDraftTransferTask(productDataList,
                giveAwayPoints);
        Response<TransferRunRespData> resp = transferClient.run(transferSalesDocData);
        assertThat(resp, successful());
        return resp.asJson();
    }

    @Step("API: ?????????????? ???????????????????????????? ???????????? ???? ??????????")
    public TransferRunRespData createConfirmedTransferTask(
            TransferProductOrderData productData,
            SalesDocumentsConst.GiveAwayPoints giveAwayPoints) {
        return createConfirmedTransferTask(Collections.singletonList(productData), giveAwayPoints);
    }

    @Step("API: ???????????????? ???????????????????????????? ???????????? ???? ?????????? ??????????????")
    public void cancelConfirmedTransferTask(String taskId) {
        String soapEndpointUrl = "https://navsvr.hq.ru.corp.leroymerlin.com:30957/LAIR_SRM_API/WS/%D0%97%D0%B5%D0%BB%D0%B5%D0%BD%D0%BE%D0%B3%D1%80%D0%B0%D0%B4/Codeunit/API3";
        String extTask = getExtTaskByTaskId(taskId);
        callSoapWebService(soapEndpointUrl, taskId, extTask);
    }

    // -------------- Search Products ------------- //

    @Step("?????????? ?????????????? ?????????????????? ?????? ???????????? ???? ????????????")
    public List<TransferSearchProductData> searchForProductsForTransfer(SearchFilters filters) {
        Response<TransferSearchProductDataList> resp = transferClient.searchForTransferProducts(
                SalesDocumentsConst.GiveAwayPoints.SALES_FLOOR);
        assertThat(resp, successful());
        List<TransferSearchProductData> result = resp.asJson().getItems();
        if (StockType.MONO_PALLET.equals(filters.getStockType())) {
            result = result.stream().filter(p -> p.getSource().get(0).getMonoPallets() != null)
                    .collect(Collectors.toList());
        }
        assertThat("???? ???????????? ???? ???????? ??????????", result, hasSize(greaterThan(0)));
        return result;
    }

    @Step("?????????? ?????????????? ?????????????????? ?????? ???????????? ???? ????????????")
    public List<TransferSearchProductData> searchForProductsForTransfer() {
        return searchForProductsForTransfer(new SearchFilters());
    }

    public enum StockType {
        MONO_PALLET, MIX_PALLET
    }

    @Data
    @Accessors(chain = true)
    public static class SearchFilters {

        private StockType stockType;
    }

}
