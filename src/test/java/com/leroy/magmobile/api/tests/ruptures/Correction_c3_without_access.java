package com.leroy.magmobile.api.tests.ruptures;

import com.leroy.magmobile.api.tests.ruptures.annotations.Section;
import com.leroy.magmobile.api.tests.ruptures.annotations.Section1;
import com.leroy.magmobile.api.tests.ruptures.annotations.Section2;
import com.leroy.utils.FileUtils;
import io.qameta.allure.AllureId;
import io.qameta.allure.Owner;
import org.testng.annotations.Test;
import static io.qameta.allure.Allure.step;

@Owner("60097126")
@Section("Lego Front 1. UI Test Cases")
@Section1("Regression Tests")
@Section2("Перебои")
public class Correction_c3_without_access {
    @Test
    @AllureId("32008")
    public void simpleCorrectionWithoutAccessTest() {
        step("Предусловия", () -> {
            step("Авторизация пройдена под пользователем 60099948 (у пользователя нет доступа к коррекции С3)", () -> {
            });
            step("У пользователя выбран 35 магазин 5 отдел", () -> {
            });
        });
        step("Открыть сессию ", () -> {
            step("В шапке указаны: \n" +
                    "- дата и время создания\n" +
                    "- количество перебоев в сессии\n" +
                    "- номер сессии\n" +
                    "Ниже список с перебоем, у перебоя имеются:\n" +
                    "- ЛМ и ШК \n" +
                    "- наименование товара\n" +
                    "- фото (если есть) \n" +
                    "Внизу экрана кнопки: \"+ ПЕРЕБОЙ\" \"ЗАВЕРШИТЬ\"", () -> {
                FileUtils.attachPicture("Открытая_сессия_для_коррекции_без_прав", "/ControlDiscrepancy/Correction_c3_without_access.jpg", ".jpeg");
            });
        });
        step("Открыть карточку товара", () -> {
             step("В карточке присутствуют: \n" +
                        "- ЛМ и ШК\n" +
                        "- наименование товара\n" +
                        "- фото (если есть) \n" +
                        "- цена \n" +
                        "- информация о наличии товара\n" +
                        "кнопка \"действия с перебоем", () -> {
                    FileUtils.attachPicture("Открытая_карточка_товара_для_коррекции_без_прав", "/ControlDiscrepancy/image1.jpg", ".jpeg");
                });
            });
        step("Тапнуть \"Назначить задачи\"", () -> {
             step("Открывается всплывающее окно\n" +
                        "- Нужно сделать\n" +
                        "- Возможные задачи:\n" +
                        "-- поставить извиняшку\n" +
                        "-- позвонить поставщику\n" +
                        "-- заказать у поставщика\n" +
                        "-- убрать ценник\n" +
                        "-- сделать коррекцию С3\n" +
                        "-- отозвать с RM\n" +
                        "-- найти товар и выложить\n" +
                        "-- наклеить красный стикер ", () -> {
                });
                FileUtils.attachPicture("Открытая_карточка_товара_для_коррекции_без_прав2", "/ControlDiscrepancy/C3_without_access_v2.jpg", ".jpeg");
            });
        step("Добавить экшен \"Сделать коррекцию С3\"", () -> {
             step("Экшен \"сделать коррекцию С3\" перемещается наверх в список \"нужно сделать\"", () -> {
                });
                FileUtils.attachPicture("Открытая_карточка_товара_для_коррекции_без_прав3", "/ControlDiscrepancy/C3_without_access_v3.jpg", ".jpeg");
            });
        step("Закрыть всплывающее окно с задачами", () -> {
             step("В карточке появился неактивный экшен\n" +
                        "\"Сделать коррекцию С3\n" +
                        "У тебя нет прав делать коррекцию С3\" ", () -> {
                });
                FileUtils.attachPicture("Открытая_карточка_товара_для_коррекции_без_прав4", "/ControlDiscrepancy/image4.png", ".png");
            });
        step("Тапнуть по экшену \"сделать коррекцию С3\"", () -> {
             step("Перехода на достоверность не происходит, из-за отсутствия доступа", () -> {
                });
            });




    }
}