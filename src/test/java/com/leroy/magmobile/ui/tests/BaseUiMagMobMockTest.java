package com.leroy.magmobile.ui.tests;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leroy.core.ContextProvider;
import com.leroy.core.configuration.Log;
import com.leroy.core.util.mountebank.MockDataPreparer;
import com.leroy.core.util.mountebank.MountebankClient;
import com.leroy.core.util.mountebank.PredicateExtend;
import com.leroy.magmobile.api.requests.CommonLegoRequest;
import com.leroy.magmobile.ui.AppBaseSteps;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.mbtest.javabank.http.core.Stub;
import org.mbtest.javabank.http.imposters.Imposter;
import org.mbtest.javabank.http.predicates.Predicate;
import org.mbtest.javabank.http.predicates.PredicateType;
import org.mbtest.javabank.http.responses.Is;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class BaseUiMagMobMockTest extends AppBaseSteps {

    private MockDataPreparer mockDataPreparer = new MockDataPreparer(4548, "magmobile");

    private MountebankClient mountebankClient = new MountebankClient("https://mountebank-dev-magfront-stage.apps.lmru.tech");
    private final int DEFAULT_IMPOSTER_PORT = 4548;
    private final String PATH_MOCK_DIRECTORY = "src" + File.separator + "main" + File.separator +
            "resources" + File.separator + "mock" + File.separator + "magmobile";

    private static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    @BeforeClass
    protected void restartDefaultImposter() throws Exception {
        mockDataPreparer.restartDefaultImposter();
    }

    /**
     * ???????????????? stub'???? ???? ???????????? json ?????????? ???? ?????????????? stubs, ?????? ?????????????????? ?????? ?????????????? (?? predicates, ?? responses)
     */
    public void setUpMockForTestCase() throws Exception {
        mockDataPreparer.setUpMockForTestCase(this.getClass().getSimpleName().toLowerCase());
    }

    protected void createStub(PredicateType requestPredicateType, CommonLegoRequest<?> request,
                              PredicateType bodyPredicateType, Object body,
                              int responseIndex) throws Exception {
        String tcId = ContextProvider.getContext().getTcId();
        String className = this.getClass().getSimpleName().toLowerCase();
        String filePath = "src/main/resources/mock/magmobile/" + className + (tcId != null ? "/" + tcId : "/default") + ".json";
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

        PredicateExtend requestPredicate = new PredicateExtend(requestPredicateType);
        requestPredicate.withMethod(request.getMethod())
                .withPath(request.getPath())
                .withQueryParameters(request.getQueryParams());

        List<Predicate> stubPredicates;
        if (requestPredicateType != null && bodyPredicateType != null) {
            PredicateExtend predicate = new PredicateExtend(PredicateType.AND);

            PredicateExtend bodyPredicate = new PredicateExtend(bodyPredicateType);
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            bodyPredicate.withBody(mapper.writeValueAsString(body));

            predicate.withPredicates(Arrays.asList(requestPredicate, bodyPredicate));
            stubPredicates = Collections.singletonList(predicate);
        } else {
            stubPredicates = Collections.singletonList(requestPredicate);
        }

        stub.addPredicates(stubPredicates);
        stub.addResponse(new Is().withBody(jsonBody.toJSONString()));

        int updSt = mountebankClient.addStub(stub, DEFAULT_IMPOSTER_PORT);
        for (int i = 0; i < 8; i++) {
            if (updSt != 200)
                updSt = mountebankClient.addStub(stub, DEFAULT_IMPOSTER_PORT);
            else
                break;
        }
        Assert.assertEquals(updSt, 200, "???? ?????????????? ???????????????? stubs ?????? " + tcId);
        Log.info("Stub is created: " + request.build(""));
    }

    protected void createStub(PredicateType requestPredicateType, CommonLegoRequest<?> request,
                              int responseIndex) throws Exception {
        createStub(requestPredicateType, request, null, null, responseIndex);
    }

}
