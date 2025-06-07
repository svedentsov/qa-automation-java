package com.svedentsov.steps.petstore;

import com.svedentsov.app.petstore.model.Order;
import com.svedentsov.steps.common.BaseSteps;
import io.qameta.allure.Step;

import java.util.Map;

import static com.svedentsov.matcher.assertions.rest.HeaderAssertions.contentType;
import static com.svedentsov.matcher.assertions.rest.StatusAssertions.statusCode;
import static io.restassured.http.ContentType.JSON;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс для выполнения шагов, связанных с операциями над заказами и инвентарем в магазине.
 * Содержит методы для создания, получения, удаления заказов и проверки состояния инвентаря.
 */
public class StoreSteps extends BaseSteps {

    /**
     * Удаляет заказ по идентификатору.
     *
     * @param orderId идентификатор заказа
     */
    @Step("Удаление заказа с идентификатором: {orderId}")
    public void deleteOrder(Long orderId) {
        rest.storeService()
                .deleteOrder(orderId)
                .shouldHave(statusCode(200));
    }

    /**
     * Получает инвентарь питомцев по статусу.
     *
     * @return карта, представляющая инвентарь
     */
    @Step("Получение инвентаря питомцев по статусу")
    public Map<String, Object> getInventory() {
        return rest.storeService()
                .getInventory()
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200))
                .getResponseAsMap();
    }

    /**
     * Получает заказ по идентификатору.
     *
     * @param orderId идентификатор заказа
     * @return объект {@link Order}, представляющий заказ
     */
    @Step("Поиск заказа по идентификатору: {orderId}")
    public Order getOrderById(Long orderId) {
        return rest.storeService()
                .getOrderById(orderId)
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200))
                .getResponseAs(Order.class);
    }

    /**
     * Проверяет, что заказ не найден по идентификатору.
     *
     * @param orderId идентификатор заказа
     */
    @Step("Проверка, что заказ не найден по идентификатору: {orderId}")
    public void getNotFoundOrderById(Long orderId) {
        rest.storeService()
                .getOrderById(orderId)
                .shouldHave(statusCode(404));
    }

    /**
     * Размещает заказ на питомца.
     *
     * @param body тело запроса, содержащее данные заказа
     * @return объект {@link Order}, представляющий размещённый заказ
     */
    @Step("Размещение заказа на питомца")
    public Order placeOrder(Order body) {
        return rest.storeService()
                .placeOrder(body)
                .shouldHave(contentType(JSON))
                .shouldHave(statusCode(200))
                .getResponseAs(Order.class);
    }

    /**
     * Проверяет, что заказ существует.
     *
     * @param order объект {@link Order} для проверки
     * @return объект StoreSteps для дальнейших действий
     */
    @Step("Проверка существования заказа")
    public StoreSteps assertOrderExists(Order order) {
        assertNotNull(order, "Заказ не должен быть null");
        return this;
    }

    /**
     * Проверяет, что идентификатор заказа соответствует ожидаемому значению.
     *
     * @param order      объект {@link Order} для проверки
     * @param expectedId ожидаемый идентификатор
     * @return объект StoreSteps для дальнейших действий
     */
    @Step("Проверка, что идентификатор заказа соответствует ожидаемому")
    public StoreSteps assertOrderIdEquals(Order order, long expectedId) {
        assertEquals(expectedId, order.id(), "Идентификатор заказа должен соответствовать ожидаемому");
        return this;
    }

    /**
     * Проверяет доступность инвентаря.
     *
     * @param inventory карта, представляющая инвентарь
     * @return объект StoreSteps для дальнейших действий
     */
    @Step("Проверка доступности инвентаря")
    public StoreSteps assertInventoryAvailable(Map<String, Object> inventory) {
        assertNotNull(inventory, "Инвентарь не должен быть null");
        Object available = inventory.get("available");
        assertNotNull(available, "Количество доступных товаров не должно быть null");
        assertTrue(available instanceof Integer, "Количество доступных товаров должно быть целым числом");
        assertTrue((Integer) available > 0, "Количество доступных товаров должно быть больше нуля");
        return this;
    }
}
