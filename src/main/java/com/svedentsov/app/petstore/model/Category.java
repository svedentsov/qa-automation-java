package com.svedentsov.app.petstore.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Модель объекта Category (категория).
 */
@Data
@Accessors(fluent = true)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    /**
     * Уникальный идентификатор категории.
     */
    @JsonProperty("id")
    private long id;
    /**
     * Название категории.
     */
    @JsonProperty("name")
    private String name;
}
