package com.leroy.core.testrail.run;

import com.leroy.core.testrail.TestRailClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Run {

    /**
     * Close test runs
     * @param projectId - project id
     * @throws Exception
     */
    private void closeTestRuns(long projectId) throws Exception {
        int i = 0;
        while (i < 100) {
            i++;
            JSONArray runs = TestRailClient.getRuns(projectId, false);
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

    private static void buildCoverageMatrix() {

    }

    public static void main(String[] args) throws Exception {
        buildCoverageMatrix();
    }

}
