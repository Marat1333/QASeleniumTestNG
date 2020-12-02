package com.leroy.magportal.ui.tests;

import com.leroy.core.ContextProvider;
import com.leroy.core.configuration.Log;
import com.leroy.core.util.mountebank.MockDataPreparer;
import com.leroy.core.util.mountebank.MountebankClient;
import com.leroy.magmobile.api.requests.CommonLegoRequest;
import com.leroy.magportal.ui.WebBaseSteps;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.mbtest.javabank.http.core.Stub;
import org.mbtest.javabank.http.predicates.Predicate;
import org.mbtest.javabank.http.predicates.PredicateType;
import org.mbtest.javabank.http.responses.Is;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class BaseMockMagPortalUiTest extends WebBaseSteps {

    private MockDataPreparer mockDataPreparer = new MockDataPreparer(4547, "magportal");

    @BeforeClass
    protected void restartDefaultImposter() throws Exception {
        mockDataPreparer.restartDefaultImposter();
    }

    @AfterMethod
    public void quiteDriver() {
        ContextProvider.quitDriver();
    }

    /**
     * Создание stub'ов на основе json файла из массива stubs, где полностью они описаны (и predicates, и responses)
     */
    public void setUpMockForTestCase() throws Exception {
        mockDataPreparer.setUpMockForTestCase(this.getClass().getSimpleName().toLowerCase());
    }

    private MountebankClient mountebankClient = new MountebankClient("https://mountebank-dev-magfront-stage.apps.lmru.tech");
    private final int DEFAULT_IMPOSTER_PORT = 4547;
    private final String PATH_MOCK_DIRECTORY = "src" + File.separator + "main" + File.separator +
            "resources" + File.separator + "mock" + File.separator + "magportal";

    private static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    protected void createStub(PredicateType predicateType, CommonLegoRequest<?> request, int responseIndex) throws Exception {
        String tcId = ContextProvider.getContext().getTcId();
        String className = this.getClass().getSimpleName().toLowerCase();
        String filePath = "src/main/resources/mock/magportal/" + className + (tcId != null ? "/" + tcId : "/default") + ".json";
        String content = readFile(filePath,
                StandardCharsets.UTF_8);
        JSONArray jsonArrayContent = (JSONArray) new JSONParser().parse(content);
        JSONObject jsonBody = null;
        for (Object obj : jsonArrayContent) {
            JSONObject jsonObj = (JSONObject) obj;
            if (jsonObj.get("index").equals((long) responseIndex))
                jsonBody = (JSONObject) jsonObj.get("body");
        }
        if (jsonBody == null)
            throw new IllegalArgumentException("Incorrect responseIndex");

        Stub stub = new Stub();

        Predicate predicate = new Predicate(predicateType);
        predicate.withMethod(request.getMethod())
                .withPath(request.getPath())
                .withQueryParameters(request.getQueryParams());

        stub.addPredicates(Collections.singletonList(predicate));
        stub.addResponse(new Is().withBody(jsonBody.toJSONString()));

        int updSt = mountebankClient.addStub(stub, DEFAULT_IMPOSTER_PORT);
        for (int i = 0; i < 8; i++) {
            if (updSt != 200)
                updSt = mountebankClient.addStub(stub, DEFAULT_IMPOSTER_PORT);
            else
                break;
        }
        Assert.assertEquals(updSt, 200, "Не удалось добавить stubs для " + tcId);
        Log.info("Stub is created: " + request.build(""));
    }


}
