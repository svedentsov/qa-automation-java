package com.svedentsov.app.petstore.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Модель объекта ApiResponse (ответ API).
 */
@Data
@Accessors(fluent = true)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    /**
     * Код ответа.
     */
    @JsonProperty("code")
    private long code;
    /**
     * Тип ответа.
     */
    @JsonProperty("type")
    private String type;
    /**
     * Сообщение ответа.
     */
    @JsonProperty("message")
    private String message;
}
