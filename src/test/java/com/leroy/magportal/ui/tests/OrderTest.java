package com.leroy.magportal.ui.tests;

import com.leroy.magportal.ui.WebBaseSteps;
import com.leroy.magportal.ui.pages.orders.OrdersPage;
import org.testng.annotations.Test;

import static com.leroy.magportal.ui.constants.OrderConst.Status.*;

public class OrderTest extends WebBaseSteps {

    @Test(description = "C22829624 Ордерс. Фильтрация по статусу заказа")
    public void testOrderFilterByStatus() throws Exception {
        // Step 1
        step("Открыть страницу с Заказами");
        OrdersPage ordersPage = loginAndGoTo(OrdersPage.class);

        // Step 2
        step("В фильтре Статус заказа выставить значение = Создан, нажать кнопку 'Показать заказы'");
        ordersPage.selectStatusFilters(CREATED);
        ordersPage.clickApplyFilters();

        // Step 3
        step("Деактивировать фильтр Создан, убрав чекбокс, нажать кнопку 'Показать заказы'");
        ordersPage.deselectStatusFilters(CREATED);
        ordersPage.clickApplyFilters();

        // Step 4
        step("Повторить шаг 2 для статусов Сборка, Сборка(пауза), Собран, Частично собран, Выдан, Отменен");
        ordersPage.selectStatusFilters(ASSEMBLY, ASSEMBLY_PAUSE, PICKED, PICKED_PARTIALLY, ISSUED, CANCELLED);
        ordersPage.clickApplyFilters();

        // Step 5
        step("Активировать статусы Создан, Сборка, Сборка(пауза), Собран, Частично собран, Выдан, Частично выдан, Отменен");
        ordersPage.selectStatusFilters(CREATED, ASSEMBLY, ASSEMBLY_PAUSE, PICKED,
                PICKED_PARTIALLY, ISSUED, ISSUED_PARTIALLY, CANCELLED);
        ordersPage.clickApplyFilters();

        // Step 6
        step("Нажать на кнопку 'Очистить фильтры' (изображена метла), нажать 'Показать заказы'");

    }
}
