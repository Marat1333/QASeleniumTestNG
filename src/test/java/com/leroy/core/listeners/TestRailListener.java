package com.leroy.core.listeners;

import com.leroy.core.configuration.Log;
import com.leroy.core.testrail.TestRailClient;
import com.leroy.core.testrail.models.ResultModel;
import com.leroy.core.testrail.models.StepResultModel;
import org.testng.ISuite;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestRailListener extends Listener {

    private Long runId;
    public static Map<String, List<StepResultModel>> STEPS_INFO; // key - Test case ID

    // This belongs to ISuiteListener and will execute before the Suite start
    @Override
    public void onStart(ISuite arg0) {
        super.onStart(arg0);
        STEPS_INFO = new HashMap<>();
        String planName = System.getProperty("plan");
        String runName = System.getProperty("run");
        if (planName != null && runName != null) {
            try {
                runId = TestRailClient.findOrCreateNewPlanRun(planName, runName);
            } catch (Exception err) {
                Log.error(err.getMessage());
            }
        }
    }

    // This belongs to ITestListener and will execute only when the test method is passed
    @Override
    public void onTestSuccess(ITestResult arg0) {
        super.onTestSuccess(arg0);
        addTestResult(arg0);
    }

    // This belongs to ITestListener and will execute only when the test method is failed
    @Override
    public void onTestFailure(ITestResult arg0) {
        super.onTestFailure(arg0);
        addTestResult(arg0);
    }

    // This belongs to ITestListener and will execute only if any of the main
    // test(@Test) get skipped
    @Override
    public void onTestSkipped(ITestResult arg0) {
        super.onTestSkipped(arg0);
        addTestResult(arg0);
    }

    private void addTestResult(ITestResult testResult) {
        String sCaseId = getTestCaseId(testResult);
        ResultModel resultModel = new ResultModel(runId, Long.valueOf(sCaseId.replaceAll("C", "")));
        resultModel.setStatus_id(convertNgStatusToTestRailStatus(testResult.getStatus()));
        resultModel.setElapsed(testResult.getEndMillis() - testResult.getStartMillis() + "s");
        resultModel.setExecutionLog(String.join("\n", Reporter.getOutput(testResult)));
        resultModel.setStepResults(STEPS_INFO.get(sCaseId));
        try {
            TestRailClient.addTestResult(resultModel);
            if (currentScreenshotPath != null) {
                TestRailClient.addAttachmentToTestResult(resultModel.getId(), currentScreenshotPath);
            }
        } catch (Exception err) {
            Log.error(err.getMessage());
        }
    }

    private int convertNgStatusToTestRailStatus(int ngStatus) {
        switch (ngStatus) {
            case 1:
                return ResultModel.ST_PASSED;
            case 2:
                return ResultModel.ST_FAILED;
            case 3:
                return ResultModel.ST_SKIPPED;
            default:
                return -1;
        }
    }

}
