package com.leroy.magportal.ui.tests;

import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.pages.picking.PickingPage;
import org.testng.annotations.Test;

public class PickingTest extends WebBaseSteps {

    @Test(description = "C23408356 Сплит сборки (зона сборки Торговый зал)")
    public void testSplitBuildShoppingRoom() throws Exception {
        PickingPage pickingPage = loginAndGoTo(PickingPage.class);

        pickingPage.clickDocumentInLeftMenu("2821 *5473");
        String s = "";
    }

}
