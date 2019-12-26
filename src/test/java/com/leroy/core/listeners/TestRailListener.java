package com.leroy.core.listeners;

import com.leroy.core.configuration.Log;
import com.leroy.core.testrail.TestRailClient;
import com.leroy.core.testrail.models.ResultModel;
import com.leroy.core.testrail.models.StepResultModel;
import org.testng.ISuite;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.util.Strings;

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
        String planName = System.getProperty("mPlan");
        String runName = System.getProperty("mRun");
        String sSuiteId = System.getProperty("mSuite");
        String sProjectId = System.getProperty("mProject");
         if(!Strings.isNullOrEmpty(sSuiteId) && !Strings.isNullOrEmpty(sProjectId)) {
             try {
                 long suiteId = Long.parseLong(sSuiteId);
                 long projectId = Long.parseLong(sProjectId);
                 //long SUITE_ID = 258L;
                 //long PROJECT_ID = 10L;
                 try {
                     if (Strings.isNotNullAndNotEmpty(planName) && Strings.isNotNullAndNotEmpty(runName)) {
                         runId = TestRailClient.findOrCreateNewPlanRun(planName, runName, projectId, suiteId);
                     } else if (Strings.isNotNullAndNotEmpty(runName)) {
                         runId = TestRailClient.findOrCreateNewRun(runName, projectId, suiteId);
                     }
                 } catch (Exception err) {
                     Log.error(err.getMessage());
                 }
             } catch (NumberFormatException e) {
                 Log.error(e.getMessage());
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
        if (runId != null) {
            String sCaseId = getTestCaseId(testResult);
            ResultModel resultModel = new ResultModel(runId, Long.valueOf(sCaseId.replaceAll("C", "")));
            resultModel.setStatus_id(convertNgStatusToTestRailStatus(testResult.getStatus()));
            long elapsed = (testResult.getEndMillis() - testResult.getStartMillis()) / 1000;
            resultModel.setElapsed(elapsed + (elapsed > 0 ? "s" : ""));
            resultModel.setExecutionLog(String.join("\n", Reporter.getOutput(testResult)));
            resultModel.setStepResults(STEPS_INFO.get(sCaseId));
            try {
                TestRailClient.addTestResult(resultModel);
                if (currentScreenshotPath != null) {
                    TestRailClient.addAttachmentToTestResult(resultModel.getId(), currentScreenshotPath);
                }
            } catch (Exception err) {
                Log.error("addTestResult() Error: " + err.getMessage());
            }
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
