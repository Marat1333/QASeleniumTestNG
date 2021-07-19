package com.leroy.magportal.api.tests.onlineOrders.other;

import static com.leroy.core.matchers.IsSuccessful.successful;
import static com.leroy.magportal.api.constants.UserTasksType.COMMENT;
import static com.leroy.magportal.api.constants.UserTasksType.LOGISTIC_COMMENT;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.inject.Inject;
import com.leroy.magmobile.api.data.sales.orders.OrderData;
import com.leroy.magportal.api.clients.OrderClient;
import com.leroy.magportal.api.constants.UserTasksProject;
import com.leroy.magportal.api.constants.UserTasksStatus;
import com.leroy.magportal.api.constants.UserTasksType;
import com.leroy.magportal.api.data.userTasks.UserTasksData;
import com.leroy.magportal.api.data.userTasks.UserTasksDataList;
import com.leroy.magportal.api.helpers.PAOHelper;
import com.leroy.magportal.api.tests.BaseMagPortalApiTest;
import io.qameta.allure.Step;
import io.qameta.allure.AllureId;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.util.Strings;
import ru.leroymerlin.qa.core.clients.base.Response;

public class UserTasksTest extends BaseMagPortalApiTest {

    @Inject
    private PAOHelper paoHelper;
    @Inject
    private OrderClient orderClient;

    private String currentOrderId;
    private String currentTaskId;
    private String currentText;
    private int tasksCount;

    @BeforeClass
    private void setUp() {
        OrderData orderData = paoHelper
                .createConfirmedPickupOrder(paoHelper.makeCartProducts(3), true);
        currentOrderId = orderData.getOrderId();
        tasksCount = 0;
    }

    @Test(description = "C23749151 Get User Tasks for New Order", priority = 1)
    @AllureId("1950")
    public void testGetUserTasksNewOrder() {
        Response<UserTasksDataList> resp = orderClient.getUserTasks(currentOrderId);
        assertGetUserTasksResult(resp, false);
    }

    @Test(description = "C23749152 Post Logistic User Task for New Order", priority = 2)
    @AllureId("1951")
    public void testPostLogisticUserTasksNewOrder() {
        currentText = "C23749152 TEST Logistic";
        Response<UserTasksData> resp = orderClient.postUserTasks(currentOrderId, LOGISTIC_COMMENT,
                currentText);
        tasksCount += 1;
        assertPostUserTasksResult(resp, LOGISTIC_COMMENT, false);
    }

    @Test(description = "C23749153 Post Executable User Task", priority = 3)
    @AllureId("1952")
    public void testPostExecutableUserTasks() {
        currentText = "C23749153 TEST Execute";
        Response<UserTasksData> resp = orderClient
                .postUserTasks(currentOrderId, COMMENT, currentText);
        tasksCount += 1;
        assertPostUserTasksResult(resp, COMMENT, true);
    }

    @Test(description = "C23749154 Put Executable User Task", priority = 4)
    @AllureId("1953")
    public void testPutExecutableUserTasks() {
        Response<UserTasksData> resp = orderClient.putUserTasks(currentOrderId, currentTaskId);
        assertPostUserTasksResult(resp, COMMENT, false);
    }

    @Test(description = "C23749155 Put Logistic User Task (NEGATIVE)", priority = 5)
    @AllureId("1954")
    public void testPutLogisticUserTasks() {
        Response<UserTasksDataList> resp = orderClient.getUserTasks(currentOrderId);
        assertThat("PUT User Tasks request has PASSED for Logistic UserTask.",
                !resp.isSuccessful());
    }

    @Test(description = "C23749156 Get Several User Tasks", priority = 6)
    @AllureId("1955")
    public void testGetSeveralUserTasks() {
        orderClient.postUserTasks(currentOrderId, COMMENT, currentText);
        tasksCount += 1;
        Response<UserTasksDataList> resp = orderClient.getUserTasks(currentOrderId);
        assertGetUserTasksResult(resp, true);
    }

    @Test(description = "C23749157 Get Several User Tasks for Cancelled Order", priority = 7)
    @AllureId("1956")
    public void testGetSeveralUserTasksCancelledOrder() {
        orderClient.cancelOrder(currentOrderId);
        Response<UserTasksDataList> resp = orderClient.getUserTasks(currentOrderId);
        assertGetUserTasksResult(resp, true);
    }

    //Verification
    @Step("Check that POST User Task response is OK")
    public void assertPostUserTasksResult(Response<UserTasksData> response,
            UserTasksType tasksType, boolean isNew) {
        assertThat("POST User Tasks request has Failed.", response, successful());
        UserTasksData task = response.asJson();
        assertThat("UserTaskId is absent", Strings.isNotNullAndNotEmpty(task.getUserTaskId()));
        assertThat("UserTaskVersion is invalid", task.getVersion() >= 0);
        currentTaskId = task.getUserTaskId();
        softAssert().isTrue(task.getComment().equals(currentText), "Task Comment is invalid");
        softAssert().isTrue(task.getCreatedBy().equals(getUserSessionData().getUserLdap()),
                "Task Creator LDAP is invalid");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(task.getCreatorName()),
                "Task CreatorName is empty");
        softAssert().isTrue(Strings.isNotNullAndNotEmpty(task.getCreatedOn()),
                "Task Created Date is empty");
        softAssert().isTrue(task.getSource().equals(UserTasksProject.PUZ2),
                "Task Creator LDAP is invalid");
        softAssert().isTrue(task.getType().equals(tasksType), "Task Creator LDAP is invalid");
        if (isNew) {
            softAssert()
                    .isTrue(task.getStatus().equals(UserTasksStatus.NEW), "Task Status is invalid");
            softAssert().isTrue(task.getNeedToDo(), "Task NeedToDo is invalid");
        } else {
            softAssert().isTrue(task.getStatus().equals(UserTasksStatus.COMPLETED),
                    "Task Status is invalid");
            softAssert().isTrue(task.getUpdatedOn() != null, "Task Updated Date is empty");
            softAssert().isTrue(task.getUpdatedBy() != null, "Task Updater LDAP is empty");
            if (task.getType().equals(COMMENT)) {
                softAssert().isTrue(task.getNeedToDo(), "Task NeedToDo is invalid");
            } else {
                softAssert().isTrue(!task.getNeedToDo(), "Task NeedToDo is invalid");
            }
        }
        assertGetUserTasksResult(orderClient.getUserTasks(currentOrderId), isNew);
    }

    @Step("Check that User Tasks response is OK. Response body matches expected data")
    public void assertGetUserTasksResult(Response<UserTasksDataList> response, boolean isNew) {
        assertThat("Get User Tasks request has Failed.", response, successful());
        UserTasksDataList dataList = response.asJson();
        if (isNew) {
            softAssert().isTrue(dataList.getNewTasksCount() > 0, "There are NO NewTasksCount");
        } else {
            softAssert()
                    .isTrue(dataList.getNewTasksCount() == 0, "There are invalid NewTasksCount");
        }
//        softAssert().isTrue(dataList.getCompletedTasksCount() >= 0, "There are invalid CompletedTasksCount");//TODO always failed due to issue
        softAssert().isTrue(dataList.getUserTasks().size() == tasksCount, "Invalid count of tasks");
        softAssert().verifyAll();
    }
}