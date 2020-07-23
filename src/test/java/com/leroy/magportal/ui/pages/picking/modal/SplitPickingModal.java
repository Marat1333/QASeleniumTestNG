package com.leroy.magportal.ui.pages.picking.modal;

import com.leroy.core.annotations.WebFindBy;
import com.leroy.core.web_elements.general.Button;
import com.leroy.core.web_elements.general.Element;
import com.leroy.magportal.ui.pages.common.MagPortalBasePage;
import com.leroy.magportal.ui.webelements.commonelements.PuzCheckBox;

public class SplitPickingModal extends MagPortalBasePage {

    private final static String MODAL_DIV_XPATH = "//div[contains(@class, 'Modal-content-container lm-puz2-Picking-SplitModal')]";

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//p")
    Element header;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'Picking-SplitModal__zones')]//button[1]",
            metaName = "Опция 'Торговый зал'")
    PuzCheckBox shoppingRoomRadioBtn;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'Picking-SplitModal__zones')]//button[2]",
            metaName = "Опция 'Склад'")
    PuzCheckBox stockRadioBtn;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'Picking-SplitModal__zones')]//button[3]",
            metaName = "Опция 'СС'")
    PuzCheckBox ssRadioBtn;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'Picking-SplitModal__footer')]//button[contains(@class, 'iconButton')]",
            metaName = "Кнопка редактирования (карандаш)")
    Button editBtn;

    @WebFindBy(xpath = MODAL_DIV_XPATH + "//div[contains(@class, 'Picking-SplitModal__footer')]//button[descendant::span[text()='Продолжить']]",
            metaName = "Кнопка Продолжить")
    Button continueBtn;


    // Actions
}
