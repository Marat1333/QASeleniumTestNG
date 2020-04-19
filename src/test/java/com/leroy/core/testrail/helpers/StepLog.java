package com.leroy.core.testrail.helpers;

import com.leroy.core.configuration.Log;
import com.leroy.core.testrail.models.ResultModel;
import com.leroy.core.testrail.models.StepResultModel;
import io.qameta.allure.model.Stage;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.qameta.allure.Allure.getLifecycle;

public class StepLog {

    private int stepCounter = 0;
    private List<StepResultModel> stepResults = new ArrayList<>();
    public StepResultModel currentStepResult;

    public void step(String message) {

        if (currentStepResult != null) {
            if (currentStepResult.getStatus_id() == ResultModel.ST_UNTESTED)
                currentStepResult.setStatus_id(ResultModel.ST_PASSED);
            getLifecycle().updateStep(currentStepResult.getUuid(), s -> s.setStatus(Status.PASSED));
            getLifecycle().stopStep(currentStepResult.getUuid());
        }

        final String uuid = UUID.randomUUID().toString();
        final StepResult allureResult = new StepResult()
                .setName(message)
                .setDescription("Description")
                .setDescriptionHtml("DescHTML")
                .setStage(Stage.PENDING);
        getLifecycle().startStep(uuid, allureResult);
        currentStepResult = new StepResultModel();
        currentStepResult.setUuid(uuid);
        currentStepResult.setStatus_id(ResultModel.ST_UNTESTED);
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
