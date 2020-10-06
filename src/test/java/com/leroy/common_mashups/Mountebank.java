package com.leroy.common_mashups;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mbtest.javabank.Client;
import org.mbtest.javabank.http.imposters.Imposter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Mountebank {

    private static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void main(String[] args) throws Exception {
        Client mountebankClient = new Client("https://mountebank-dev-magfront-stage.apps.lmru.tech");
        int count = mountebankClient.getImposterCount();
        int deleteSt = mountebankClient.deleteAllImposters();

        /*Is defaultResp = new Is();
        defaultResp.withStatusCode(400);
        defaultResp.withBody("eRrOr");

        Stub defaultStub = new Stub();
        defaultStub.addResponse(defaultResp);

        Stub mainStub = new Stub();
        Is mainResp = new Is();
        mainResp.withBody("{\"message\":\"pong2\"}");
        mainStub.addResponse(mainResp);

        Predicate predicate = new Predicate(PredicateType.AND);
        predicate.
        mainStub.addPredicates();

        Imposter imposter = new Imposter();
        imposter.onPort(4545);
//        imposter.addStub(mainStub);
        imposter.addStub(defaultStub);*/

        String content = readFile("src/main/resources/mock/magmobile_default_imposter.json", StandardCharsets.UTF_8);
        Imposter imposter = Imposter.fromJSON((JSONObject) new JSONParser().parse(content));

        int st = mountebankClient.createImposter(imposter);
        String s = "";
    }

}
