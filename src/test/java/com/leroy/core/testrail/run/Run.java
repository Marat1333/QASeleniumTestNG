package com.leroy.core.testrail.run;

import com.leroy.core.testrail.TestRailClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Run {

    public static void main(String[] args) throws Exception {
        int i = 0;
        while (i < 100) {
            i++;
            JSONArray runs = TestRailClient.getRuns(10L, false);
            for (Object run : runs) {
                long id = (long) ((JSONObject) run).get("id");
                if (id < 45000) {
                    try {
                        System.out.println("Id=" + id);
                        TestRailClient.closeRun(id);
                    } catch (Exception err) {
                        String s = "";
                    }
                }
            }
        }
    }

}
