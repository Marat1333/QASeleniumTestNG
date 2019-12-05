package com.leroy.core.testrail;

import com.leroy.core.testrail.api.APIClient;
import com.leroy.core.testrail.api.APIException;
import com.leroy.core.testrail.models.PlanEntryModel;
import com.leroy.core.testrail.models.PlanModel;
import com.leroy.core.testrail.models.ResultModel;
import com.leroy.core.testrail.models.RunModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestRailClient {

    static APIClient apiClient = new APIClient("https://elbrus.testrail.net/"); // TODO

    public static void main(String args[]) throws Exception {
        String PLAN_NAME = "DEBUG_SPECIFIC_PLAN2";
        String RUN_NAME = "DEBUG_SPECIFIC_RUN2";
        Long runId = findOrCreateNewPlanRun(PLAN_NAME, RUN_NAME);
        ResultModel resultModel = new ResultModel(runId, 22819089L);
        resultModel.setStatus_id(1);
        resultModel.setElapsed("15s");
        resultModel.setExecutionLog("2222");
        addTestResult(resultModel);
    }

    private static Long findOrCreateNewPlanRun(String planName, String runName) throws Exception {
        long SUITE_ID = 258L;
        long PROJECT_ID = 10L;
        Long planId = findIdFromJSONArrayByName(getPlans(PROJECT_ID), planName);
        PlanModel planModel = new PlanModel();
        if (planId != null) {
            planModel.setId(planId);
        } else {
            planModel.setProject_id(PROJECT_ID);
            planModel.setName(planName);
            addPlan(planModel);
        }
        //Long unixTimestampOneDayAgo = getCurrentUnixTimestampWithOffsetBack(3);
        //Long runId = findIdFromJSONArrayByName(getRuns(PROJECT_ID, unixTimestampOneDayAgo), runName);
        Long runId = findIdFromJSONArrayByName(getRunsFromPlan(planModel.getId()), runName);
        if (runId == null) {
            PlanEntryModel planEntryModel = new PlanEntryModel(planModel.getId());
            planEntryModel.setSuite_id(SUITE_ID);
            planEntryModel.setName(runName);

            List<RunModel> runModels = new ArrayList<>();
            RunModel run = new RunModel();
            runModels.add(run);

            planEntryModel.setRuns(runModels);

            addPlanEntry(planEntryModel);
            runId = planEntryModel.getRuns().get(0).getId();
        }
        return runId;
    }

    public static void addRun(RunModel run) throws IOException, APIException {
        Object obj = apiClient.sendPost("add_run/" + run.getProject_id(), run.getData());
        run.setId(Long.valueOf(((JSONObject) obj).get("id").toString()));
    }

    public static void addPlan(PlanModel plan) throws IOException, APIException {
        Object obj = apiClient.sendPost("add_plan/" + plan.getProject_id(), plan.getData());
        plan.setId(Long.valueOf(((JSONObject) obj).get("id").toString()));
    }

    public static void addPlanEntry(PlanEntryModel planEntryModel) throws IOException, APIException {
        Object obj = apiClient.sendPost("add_plan_entry/" + planEntryModel.getId(), planEntryModel.getData());
        int i = 0;
        for (Object r : (JSONArray) ((JSONObject) obj).get("runs")) {
            JSONObject run = (JSONObject) r;
            planEntryModel.getRuns().get(i).setId(Long.valueOf(run.get("id").toString()));
            i++;
        }
    }

    public static void addTestResult(ResultModel resultModel) throws IOException, APIException {
        Object obj = apiClient.sendPost("add_result_for_case/" +
                resultModel.getRun_id() + "/" + resultModel.getCase_id(), resultModel.getData());
        resultModel.setId(Long.valueOf(((JSONObject) obj).get("id").toString()));
    }

    public static JSONArray getPlans(Long projectId) throws IOException, APIException, InterruptedException {
        Object obj = apiClient.sendGet("get_plans/" + projectId);
        return ((JSONArray) obj);
    }

    public static JSONArray getRuns(Long projectId) throws IOException, APIException, InterruptedException {
        Object obj = apiClient.sendGet("get_runs/" + projectId);
        return ((JSONArray) obj);
    }

    public static JSONArray getRunsFromPlan(Long planId) throws IOException, APIException, InterruptedException {
        Object obj = apiClient.sendGet("get_plan/" + planId);
        JSONArray planEntries = (JSONArray) ((JSONObject)obj).get("entries");
        JSONArray runsArray = new JSONArray();
        for (Object entry : planEntries) {
            runsArray.add(((JSONArray)((JSONObject)entry).get("runs")).get(0));
        }
        return runsArray;
    }

    public static JSONArray getRuns(Long projectId, Long createdAfterTimestamp) throws IOException, APIException, InterruptedException {
        Object obj = apiClient.sendGet("get_runs/" + projectId + "&created_after=" + createdAfterTimestamp);
        return ((JSONArray) obj);
    }

    private static Long findIdFromJSONArrayByName(JSONArray jsonArray, String name) {
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject)obj;
            if (jsonObject.get("name").toString().equals(name))
                return Long.valueOf(jsonObject.get("id").toString());
        }
        return null;
    }

    private static Long getCurrentUnixTimestampWithOffsetBack(int days) {
        return System.currentTimeMillis() / 1000 - days * 24 * 60 * 60;
    }

}
