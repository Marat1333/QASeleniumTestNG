package com.leroy.magportal.ui.tests;

import com.leroy.core.ContextProvider;
import com.leroy.core.util.MountebankClient;
import com.leroy.magmobile.api.requests.CommonLegoRequest;
import com.leroy.magportal.ui.WebBaseSteps;
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
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class BaseMockUiTest extends WebBaseSteps {

    private MountebankClient mountebankClient = new MountebankClient("https://mountebank-dev-magfront-stage.apps.lmru.tech");
    private final int DEFAULT_IMPOSTER_PORT = 4547;

    private static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    @BeforeClass
    protected void restartDefaultImposter() throws Exception {
        int deleteSt = mountebankClient.deleteAllImposters();
        Assert.assertEquals(deleteSt, 200, "Не удалось удалить Imposter");

        String defaultContent = readFile("src/main/resources/mock/magportal_default_imposter.json", StandardCharsets.UTF_8);
        Imposter imposter = Imposter.fromJSON((JSONObject) new JSONParser().parse(defaultContent));
        int stCreate = mountebankClient.createImposter(imposter);
        Assert.assertEquals(stCreate, 201, "Не удалось создать default'ый Imposter");
    }

    protected void createStub(PredicateType predicateType, CommonLegoRequest<?> request, int responseIndex) throws Exception {
        String tcId = ContextProvider.getContext().getTcId();
        String content = readFile("src/main/resources/mock/magportal/" + tcId + ".json", StandardCharsets.UTF_8);
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
        Assert.assertEquals(updSt, 200, "Не удалось добавить stubs для " + tcId);
    }


}
