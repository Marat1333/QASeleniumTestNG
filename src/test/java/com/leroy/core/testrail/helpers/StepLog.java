package com.leroy.core.testrail.helpers;

import com.leroy.core.configuration.Log;
import com.leroy.core.testrail.models.ResultModel;
import com.leroy.core.testrail.models.StepResultModel;

import java.util.ArrayList;
import java.util.List;

public class StepLog {

    private int stepCounter = 0;
    private List<StepResultModel> stepResults = new ArrayList<>();
    public StepResultModel currentStepResult;

    public void step(String message) {
        currentStepResult = new StepResultModel();
        currentStepResult.setStatus_id(ResultModel.ST_PASSED);
        currentStepResult.setContent(message);
        stepResults.add(currentStepResult);
        stepCounter++;
        Log.step(stepCounter + ". " + message);
    }

    public void assertFail(String message) {
        if (currentStepResult != null)
            currentStepResult.setStatus_id(ResultModel.ST_FAILED);
        Log.assertFail(message);
    }

    public StepResultModel getCurrentStepResult() {
        return currentStepResult;
    }

    public List<StepResultModel> getStepResults() {
        return stepResults;
    }
}
