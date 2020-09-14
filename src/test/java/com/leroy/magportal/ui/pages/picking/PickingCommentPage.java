package com.leroy.magportal.ui.pages.picking;

import com.leroy.constants.DefectConst;
import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.TextArea;
import io.qameta.allure.Step;

public class PickingCommentPage extends PickingPage {

    @WebFindBy(id = "textAreaId", metaName = "Поле Комментарий")
    TextArea commentFld;

    // Verifications

    @Step("Проверить, что комментарий = {text}")
    public PickingCommentPage shouldCommentIs(String text) {
        if (!DefectConst.PUZ2_2551) {
            anAssert.isElementVisible(commentFld);
            anAssert.isEquals(commentFld.getText().trim(), text.trim(),
                    "Неверный комментарий");
        }
        return this;
    }
}
