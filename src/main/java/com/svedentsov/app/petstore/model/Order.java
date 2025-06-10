package com.svedentsov.app.petstore.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.svedentsov.app.petstore.enums.OrderStatus;
import com.svedentsov.utils.LocalDateTimeDeserializer;
import com.svedentsov.utils.LocalDateTimeSerializer;

import java.time.LocalDateTime;

/**
 * Модель объекта заказа (Order).
 */
@Data
@Accessors(fluent = true)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /**
     * Уникальный идентификатор заказа.
     */
    @JsonProperty("id")
    private long id;

    /**
     * Идентификатор питомца, связанного с заказом.
     */
    @JsonProperty("petId")
    private long petId;

    /**
     * Количество товаров в заказе.
     */
    @JsonProperty("quantity")
    private int quantity;

    /**
     * Дата доставки заказа.
     * Для сериализации и десериализации используется формат ISO {@code yyyy-MM-dd'T'HH:mm:ss}.
     */
    @JsonProperty("shipDate")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime shipDate;

    /**
     * Статус заказа.
     */
    @JsonProperty("status")
    private OrderStatus status;

    /**
     * Флаг, указывающий на завершение заказа.
     */
    @JsonProperty("complete")
    private boolean complete;
}
