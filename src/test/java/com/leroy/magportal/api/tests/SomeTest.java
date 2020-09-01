package com.leroy.magportal.api.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.leroy.core.api.ThreadApiClient;
import com.leroy.core.configuration.Log;
import com.leroy.magportal.api.clients.CatalogSearchClient;
import com.leroy.magportal.api.helpers.BitrixHelper;
import com.leroy.magportal.api.helpers.PaymentHelper;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;
import ru.leroymerlin.qa.core.clients.tunnel.TunnelClient;
import ru.leroymerlin.qa.core.clients.tunnel.data.BitrixSolutionPayload;
import ru.leroymerlin.qa.core.clients.tunnel.data.BitrixSolutionResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Тестовый класс надо будет переименовать и дать соответствующее название
public class SomeTest extends BaseMagPortalApiTest {

    @Inject
    CatalogSearchClient paymentHelper;

    @Inject
    private BitrixHelper bitrixHelper;

    @Test(description = "C1 Название теста")
    public void test() throws Exception {
//        bitrixHelper.createOnlineOrders();
//        String id = bitrixHelper.createOnlineOrder();
//        paymentHelper.makePaymentCard(id);
    }

    /**
     * Создает ONLINE ордер
     *
     * @return task id
     */

    }