package com.leroy.common_mashups;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mbtest.javabank.Client;
import org.mbtest.javabank.http.core.Stub;
import org.mbtest.javabank.http.imposters.Imposter;
import org.mbtest.javabank.http.predicates.Predicate;
import org.mbtest.javabank.http.predicates.PredicateType;
import org.mbtest.javabank.http.responses.Is;
import org.mbtest.javabank.http.responses.Response;

public class Mountebank {

    public static void main(String[] args) throws ParseException {
        Client mountebankClient = new Client("https://mountebank-dev-magfront-stage.apps.lmru.tech");
        int count = mountebankClient.getImposterCount();
        int deleteSt = mountebankClient.deleteAllImposters();

/*        Is defaultResp = new Is();
        defaultResp.withStatusCode(400);
        defaultResp.withBody("eRrOr");

        Stub defaultStub = new Stub();
        defaultStub.addResponse(defaultResp);

//        Stub mainStub = new Stub();
//        Is mainResp = new Is();
//        mainResp.withBody("{\"message\":\"pong2\"}");
//        mainStub.addResponse(mainResp);

//        Predicate predicate = new Predicate(PredicateType.AND);
//        predicate.
//        mainStub.addPredicates();

        Imposter imposter = new Imposter();
        imposter.onPort(4545);
//        imposter.addStub(mainStub);
        imposter.addStub(defaultStub);*/

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse("{\n" +
                "    \"port\" : 4547,\n" +
                "    \"protocol\": \"http\",\n" +
                "    \"stubs\": [\n" +
                "        {\n" +
                "            \"predicates\": [\n" +
                "                {\n" +
                "                    \"and\" : [\n" +
                "                        {\n" +
                "                            \"equals\": {\n" +
                "                                \"path\" : \"/test\",\n" +
                "                                \"method\": \"GET\"\n" +
                "                            }\n" +
                "                        }\n" +
                "                    ]\n" +
                "                }\n" +
                "            ],\n" +
                "\n" +
                "            \"responses\": [\n" +
                "                {\"is\" : {\"body\":{\"message\":\"pong2\"} } }\n" +
                "            ]\n" +
                "        },\n" +
                "\n" +
                "        {\n" +
                "            \"responses\": [\n" +
                "                { \"is\" : {\"statusCode\": 400 } }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}");

        Imposter imposter = Imposter.fromJSON(json);

        int st = mountebankClient.createImposter(imposter);
        String s = "";
    }

}
