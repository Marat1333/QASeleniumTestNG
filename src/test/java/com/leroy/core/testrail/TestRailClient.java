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
        String PLAN_NAME = "DEBUG_SPECIFIC_PLAN1";
        String RUN_NAME = "DEBUG_SPECIFIC_RUN1";
        getPlans(10L);
        //newTestRun();
        ResultModel resultModel = new ResultModel(39706L, 22819089L);
        resultModel.setStatus_id(1);
        resultModel.setElapsed("15s");
        resultModel.setExecutionLog("2222");
        addTestResult(resultModel);
    }

    private static void findOrCreateNewPlanRun(String planName, String runName) throws Exception {
        long SUITE_ID = 258L;
        long PROJECT_ID = 10L;
        PlanModel planModel = new PlanModel();
        planModel.setProject_id(PROJECT_ID);
        planModel.setName("Debug_Plan_2");
        addPlan(planModel);

        PlanEntryModel planEntryModel = new PlanEntryModel(planModel.getId());
        planEntryModel.setSuite_id(SUITE_ID);
        planEntryModel.setName("Debug_Run1");

        List<RunModel> runModels = new ArrayList<>();
        RunModel run = new RunModel();
        runModels.add(run);

        planEntryModel.setRuns(runModels);

        addPlanEntry(planEntryModel);

        ResultModel resultModel = new ResultModel(
                planEntryModel.getRuns().get(0).getId(), 22819089L);
        resultModel.setStatus_id(5);
        resultModel.setElapsed("15s");
        resultModel.setExecutionLog("Logggg");
        addTestResult(resultModel);

        System.out.println("11111");
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

    public static void getPlans(Long projectId) throws IOException, InterruptedException {
        Object obj = apiClient.sendGet("get_plans/" + projectId);
        String s = "";
    }

}
