package com.leroy.magmobile.api.tests.salesdoc;

import static com.leroy.core.matchers.Matchers.successful;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import com.google.inject.Inject;
import com.leroy.magmobile.api.clients.PickingTaskClient;
import com.leroy.magmobile.api.data.sales.picking.PickingTaskData;
import com.leroy.magmobile.api.data.sales.picking.PickingTaskDataList;
import com.leroy.magmobile.api.data.sales.picking.PickingTaskFilter;
import com.leroy.magmobile.api.tests.BaseProjectApiTest;
import org.testng.annotations.Test;
import ru.leroymerlin.qa.core.clients.base.Response;

public class PickingActionTest extends BaseProjectApiTest {

    @Inject
    private PickingTaskClient pickingTaskClient;

    @Test(description = "C23195055 Picking search without filters")
    public void testPickingSearchWithoutFilters() {
        int pageSize = 14;
        Response<PickingTaskDataList> resp = pickingTaskClient.searchForTasks(new PickingTaskFilter(), 1, pageSize);
        assertThat(resp, successful());
        PickingTaskDataList dataList = resp.asJson();
        assertThat("count task items", dataList.getItems(), hasSize(pageSize));
        for (PickingTaskData pickingTaskData : dataList.getItems()) {
            assertThat("taskId", pickingTaskData.getTaskId(), notNullValue());
            assertThat("shopId", pickingTaskData.getShopId(), greaterThan(0));
            assertThat("taskStatus", pickingTaskData.getTaskStatus(), not(emptyOrNullString()));
            assertThat("pointOfGiveAway", pickingTaskData.getPointOfGiveAway(), not(emptyOrNullString()));
            assertThat("dateOfGiveAway", pickingTaskData.getDateOfGiveAway(), notNullValue());
            assertThat("pickingZone", pickingTaskData.getPickingZone(), not(emptyOrNullString()));
            assertThat("documentVersion", pickingTaskData.getDocumentVersion(), greaterThan(0));
            assertThat("buId", pickingTaskData.getBuId(), greaterThan(0));
            assertThat("priority", pickingTaskData.getPriority(), not(emptyOrNullString()));
            assertThat("products", pickingTaskData.getProducts(), hasSize(greaterThan(0)));
        }
    }

}
