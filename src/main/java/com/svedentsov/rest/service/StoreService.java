package com.svedentsov.rest.service;

import com.svedentsov.rest.helper.RestExecutor;
import com.svedentsov.app.petstore.model.Order;

import static com.svedentsov.rest.enums.Endpoint.*;
import static io.restassured.http.ContentType.JSON;
import static com.svedentsov.rest.enums.BaseUrl.PETSTORE;

/**
 * Сервис для взаимодействия с API магазина.
 */
public class StoreService extends AbstractService {

    /**
     * Удаление заказа по идентификатору.
     *
     * @param orderId идентификатор заказа для удаления
     * @return объект {@link RestExecutor} для выполнения запроса
     */
    public RestExecutor deleteOrder(Long orderId) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON)
                .addPathParam("orderId", String.valueOf(orderId));
        request.delete(ORDER_BY_ID.path());
        return request;
    }

    /**
     * Получение информации о запасах товаров в магазине.
     *
     * @return объект {@link RestExecutor} для выполнения запроса
     */
    public RestExecutor getInventory() {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON);
        request.get(ORDER_INVENTORY.path());
        return request;
    }

    /**
     * Получение информации о заказе по его идентификатору.
     *
     * @param orderId идентификатор заказа, информацию о котором необходимо получить
     * @return объект {@link RestExecutor} для выполнения запроса
     */
    public RestExecutor getOrderById(Long orderId) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON)
                .addPathParam("orderId", String.valueOf(orderId));
        request.get(ORDER_BY_ID.path());
        return request;
    }

    /**
     * Размещение нового заказа.
     *
     * @param order объект заказа, который необходимо разместить
     * @return объект {@link RestExecutor} для выполнения запроса
     */
    public RestExecutor placeOrder(Order order) {
        RestExecutor request = new RestExecutor(PETSTORE.url())
                .setContentType(JSON)
                .setBody(order);
        request.post(ORDER.path());
        return request;
    }
}
