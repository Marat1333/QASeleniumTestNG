package com.leroy.core.util.mountebank;

import com.leroy.core.ContextProvider;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.mbtest.javabank.http.imposters.Imposter;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MockDataPreparer {

    private MountebankClient mountebankClient = new MountebankClient("https://mountebank-dev-magfront-stage.apps.lmru.tech");
    private String mainMockDirectory;
    private int imposterPort;
    private final String PATH_MOCK_DIRECTORY = "src" + File.separator + "main" + File.separator +
            "resources" + File.separator + "mock";


    public MockDataPreparer(int port, String mainMockDirectory) {
        this.imposterPort = port;
        this.mainMockDirectory = mainMockDirectory;
    }

    private static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public void restartDefaultImposter() throws Exception {
        int deleteSt = mountebankClient.deleteAllImposters();
        Assert.assertEquals(deleteSt, 200, "Не удалось удалить Imposter");

        String defaultContent = readFile("src/main/resources/mock/" + mainMockDirectory + "_default_imposter.json",
                StandardCharsets.UTF_8);
        Imposter imposter = Imposter.fromJSON((JSONObject) new JSONParser().parse(defaultContent));
        int stCreate = mountebankClient.createImposter(imposter);
        if (stCreate != 201)
            stCreate = mountebankClient.createImposter(imposter);
        if (stCreate != 201)
            stCreate = mountebankClient.createImposter(imposter);
        Assert.assertEquals(stCreate, 201, "Не удалось создать default'ый Imposter");
    }

    public void setUpMockForTestCase(String className) throws Exception {
        String tcId = ContextProvider.getContext().getTcId();
        String defaultMockPath = PATH_MOCK_DIRECTORY + File.separator + mainMockDirectory + File.separator + className +
                File.separator + "default.json";
        if (new File(defaultMockPath).exists()) {
            String defaultContent = readFile(defaultMockPath, StandardCharsets.UTF_8);
            int updSt = mountebankClient.addStubsFromArray((JSONObject) new JSONParser().parse(defaultContent), imposterPort);
            Assert.assertEquals(updSt, 200, "Не удалось добавить default stubs для " + tcId);
        }
        String content = readFile(PATH_MOCK_DIRECTORY + File.separator + mainMockDirectory + File.separator +
                        className + File.separator + tcId + ".json",
                StandardCharsets.UTF_8);
        int updSt = mountebankClient.addStubsFromArray((JSONObject) new JSONParser().parse(content), imposterPort);
        Assert.assertEquals(updSt, 200, "Не удалось добавить stubs для " + tcId);
    }


}
