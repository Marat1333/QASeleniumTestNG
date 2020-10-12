package com.leroy.core.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.mbtest.javabank.Client;
import org.mbtest.javabank.http.core.Stub;

public class MountebankClient extends Client {

    public MountebankClient(String baseUrl) {
        super(baseUrl);
    }

    public int addStubsFromArray(JSONObject jsonStubsBody, int port) {
        try {
            int status = 0;
            JSONArray stubs = (JSONArray) jsonStubsBody.get("stubs");
            for (Object obj : stubs) {
                JSONObject oneStubBody = (JSONObject) obj;
                JSONObject stub = new JSONObject();
                stub.put("index", 0);
                stub.put("stub", oneStubBody);

                HttpResponse<JsonNode> response = Unirest.post(this.baseUrl + "/imposters/" + port + "/stubs")
                        .body(stub.toJSONString()).asJson();
                status = response.getStatus();
                if (status != 200)
                    return status;
            }
            return status;
        } catch (UnirestException var3) {
            return 500;
        }
    }

    public int addStub(Stub stub, int port) {
        try {
            JSONObject stubJsonObj = new JSONObject();
            stubJsonObj.put("index", 0);
            stubJsonObj.put("stub", stub);
            HttpResponse<JsonNode> response = Unirest.post(this.baseUrl + "/imposters/" + port + "/stubs")
                    .body(stubJsonObj.toJSONString()).asJson();
            int status = response.getStatus();
            if (status != 200)
                return status;
            return status;
        } catch (UnirestException var3) {
            return 500;
        }
    }

}
