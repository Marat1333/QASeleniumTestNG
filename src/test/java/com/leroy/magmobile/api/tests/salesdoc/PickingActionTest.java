package com.leroy.magmobile.api.tests.salesdoc;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import com.google.inject.Inject;
import com.leroy.magmobile.api.clients.PickingTaskClient;
import com.leroy.magmobile.api.data.sales.picking.PickingTaskData;
import com.leroy.magmobile.api.data.sales.picking.PickingTaskDataList;
import com.leroy.magmobile.api.data.sales.picking.PickingTaskFilter;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import io.qameta.allure.Issue;
import io.qameta.allure.TmsLink;
import org.testng.annotations.Test;
import org.testng.util.Strings;
import ru.leroymerlin.qa.core.clients.base.Response;

public class PickingActionTest extends BaseProjectApiTest {

    @Inject
    private PickingTaskClient pickingTaskClient;

    @Issue("Backend contains broken tasks")
    @Test(description = "C23195055 Picking search without filters", enabled = false)//TODO enable when data fixed
    @TmsLink("3481")
    public void testPickingSearchWithoutFilters() {
        int pageSize = 14;
        Response<PickingTaskDataList> resp = pickingTaskClient.searchForTasks(new PickingTaskFilter(), 1, pageSize);
        assertThat(resp, successful());
        PickingTaskDataList dataList = resp.asJson();
        assertThat("count task items", dataList.getItems(), hasSize(pageSize));
        for (PickingTaskData pickingTaskData : dataList.getItems()) {
            String desc = String.format("TaskId: %s, invalid field value: ", pickingTaskData.getTaskId());
            softAssert().isTrue(Strings.isNotNullAndNotEmpty(pickingTaskData.getTaskId()), desc + "taskId");
            softAssert().isTrue(pickingTaskData.getShopId() > 0, desc + "shopId");
            softAssert().isTrue(Strings.isNotNullAndNotEmpty(pickingTaskData.getTaskStatus()), desc + "taskStatus");
            softAssert().isTrue(Strings.isNotNullAndNotEmpty(pickingTaskData.getPointOfGiveAway()), desc + "pointOfGiveAway");
            softAssert().isTrue(Strings.isNotNullAndNotEmpty(pickingTaskData.getDateOfGiveAway().toString()), desc + "dateOfGiveAway");
            softAssert().isTrue(Strings.isNotNullAndNotEmpty(pickingTaskData.getPickingZone()), desc + "pickingZone");
            softAssert().isTrue(pickingTaskData.getDocumentVersion() > 0, desc + "documentVersion");
            softAssert().isTrue(pickingTaskData.getBuId() > 0, desc + "buId");
            softAssert().isTrue(Strings.isNotNullAndNotEmpty(pickingTaskData.getPriority()), desc + "priority");
            softAssert().isTrue(pickingTaskData.getProducts().size() > 0, desc + "products");
        }
        softAssert().verifyAll();
    }

}
