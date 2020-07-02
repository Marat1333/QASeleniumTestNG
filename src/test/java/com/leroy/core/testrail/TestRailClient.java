package com.leroy.core.testrail;

import com.leroy.core.configuration.DriverFactory;
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
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class TestRailClient {

    static APIClient apiClient;

    static {
        Map<String, Object> properties = (Map<String, Object>) DriverFactory
                .loadPropertiesFromFile("src/main/resources/testrail.yml");
        Map<String, Object> settings = (Map<String, Object>) properties.get("settings");
        apiClient = new APIClient(settings.get("url").toString());
        apiClient.setUser(settings.get("user").toString());
        apiClient.setPassword(new String(
                Base64.getDecoder().decode(settings.get("password").toString())));
    }

    public static Long findOrCreateNewPlanRun(String planName, String runName, long projectId, long suiteId)
            throws Exception {
        Long planId = findIdFromJSONArrayByName(getPlans(projectId), planName);
        PlanModel planModel = new PlanModel();
        if (planId != null) {
            planModel.setId(planId);
        } else {
            planModel.setProject_id(projectId);
            planModel.setName(planName);
            addPlan(planModel);
        }
        //Long unixTimestampOneDayAgo = getCurrentUnixTimestampWithOffsetBack(3);
        //Long runId = findIdFromJSONArrayByName(getRuns(PROJECT_ID, unixTimestampOneDayAgo), runName);
        Long runId = findIdFromJSONArrayByName(getRunsFromPlan(planModel.getId()), runName);
        if (runId == null) {
            PlanEntryModel planEntryModel = new PlanEntryModel(planModel.getId());
            planEntryModel.setSuite_id(suiteId);
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

    public static Long findOrCreateNewRun(String runName, long projectId, long suiteId)
            throws Exception {

        Long runId = findIdFromJSONArrayByName(getRuns(projectId), runName);
        if (runId == null) {
            RunModel run = new RunModel();
            run.setSuite_id(suiteId);
            run.setName(runName);
            run.setProject_id(projectId);
            addRun(run);
            runId = run.getId();
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

    public static Long addAttachmentToTestResult(Long testResultId, String attachmentFilePath) throws IOException, APIException {
        JSONObject json = (JSONObject) apiClient.sendPost("add_attachment_to_result/" + testResultId,
                attachmentFilePath);
        return Long.valueOf(json.get("attachment_id").toString());
    }

    public static JSONArray getPlans(Long projectId) throws IOException, APIException, InterruptedException {
        Object obj = apiClient.sendGet("get_plans/" + projectId);
        return ((JSONArray) obj);
    }

    public static JSONArray getRuns(Long projectId, boolean isCompleted) throws IOException, APIException, InterruptedException {
        Object obj = apiClient.sendGet("get_runs/" + projectId + "&is_completed=" + (isCompleted ? "1" : "0"));
        return ((JSONArray) obj);
    }

    public static JSONArray getRuns(Long projectId) throws IOException, APIException, InterruptedException {
        Object obj = apiClient.sendGet("get_runs/" + projectId);
        return ((JSONArray) obj);
    }

    public static JSONArray getRunsFromPlan(Long planId) throws IOException, APIException, InterruptedException {
        Object obj = apiClient.sendGet("get_plan/" + planId);
        JSONArray planEntries = (JSONArray) ((JSONObject) obj).get("entries");
        JSONArray runsArray = new JSONArray();
        for (Object entry : planEntries) {
            runsArray.add(((JSONArray) ((JSONObject) entry).get("runs")).get(0));
        }
        return runsArray;
    }

    public static JSONArray getRuns(Long projectId, Long createdAfterTimestamp) throws IOException, APIException, InterruptedException {
        Object obj = apiClient.sendGet("get_runs/" + projectId + "&created_after=" + createdAfterTimestamp);
        return ((JSONArray) obj);
    }

    public static JSONObject closeRun(Long runId) throws Exception {
        Object obj = apiClient.sendPost("close_run/" + runId, null);
        return ((JSONObject) obj);
    }

    private static Long findIdFromJSONArrayByName(JSONArray jsonArray, String name) {
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            if (jsonObject.get("name").toString().equals(name))
                return Long.valueOf(jsonObject.get("id").toString());
        }
        return null;
    }

    private static Long getCurrentUnixTimestampWithOffsetBack(int days) {
        return System.currentTimeMillis() / 1000 - days * 24 * 60 * 60;
    }

}
