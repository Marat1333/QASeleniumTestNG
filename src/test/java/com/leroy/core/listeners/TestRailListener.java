package com.leroy.core.listeners;

import com.leroy.core.configuration.Log;
import com.leroy.core.testrail.TestRailClient;
import com.leroy.core.testrail.models.ResultModel;
import org.testng.ISuite;
import org.testng.ITestResult;
import org.testng.Reporter;

public class TestRailListener extends Listener {

    private Long runId;

    // This belongs to ISuiteListener and will execute before the Suite start
    @Override
    public void onStart(ISuite arg0) {
        super.onStart(arg0);
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
        updateTestResult(arg0);
    }

    // This belongs to ITestListener and will execute only when the test method is failed
    @Override
    public void onTestFailure(ITestResult arg0) {
        super.onTestFailure(arg0);
        updateTestResult(arg0);
    }

    // This belongs to ITestListener and will execute only if any of the main
    // test(@Test) get skipped
    @Override
    public void onTestSkipped(ITestResult arg0) {
        super.onTestSkipped(arg0);
        updateTestResult(arg0);
    }

    private void updateTestResult(ITestResult testResult) {
        String sCaseId = getTestCaseId(testResult);
        ResultModel resultModel = new ResultModel(runId, Long.valueOf(sCaseId.replaceAll("C", "")));
        resultModel.setStatus_id(convertNgStatusToTestRailStatus(testResult.getStatus()));
        resultModel.setElapsed(String.valueOf(testResult.getEndMillis() - testResult.getStartMillis()));
        resultModel.setExecutionLog(String.join("\n", Reporter.getOutput(testResult)));
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
