package com.leroy.magmobile.ui.tests;

import com.leroy.core.ContextProvider;
import com.leroy.core.util.MountebankClient;
import com.leroy.magmobile.ui.AppBaseSteps;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.mbtest.javabank.http.imposters.Imposter;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class BaseUiMagMobMockTest extends AppBaseSteps {

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

        String defaultContent = readFile("src/main/resources/mock/magmobile_default_imposter.json", StandardCharsets.UTF_8);
        Imposter imposter = Imposter.fromJSON((JSONObject) new JSONParser().parse(defaultContent));
        int stCreate = mountebankClient.createImposter(imposter);
        Assert.assertEquals(stCreate, 201, "Не удалось создать default'ый Imposter");
    }

    @BeforeMethod
    public void setUpMockForTest() throws Exception {
        String tcId = ContextProvider.getContext().getTcId();
        String content = readFile("src/main/resources/mock/magmobile/catalog_search/" + tcId + ".json", StandardCharsets.UTF_8);
        int updSt = mountebankClient.addStubs((JSONObject) new JSONParser().parse(content), DEFAULT_IMPOSTER_PORT);
        Assert.assertEquals(updSt, 200, "Не удалось добавить stubs для " + tcId);
    }

}
