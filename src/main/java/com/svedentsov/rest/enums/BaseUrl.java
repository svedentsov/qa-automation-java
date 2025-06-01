package com.svedentsov.rest.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Перечисление, представляющее базовые URL для различных сервисов.
 */
@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum BaseUrl {

    PETSTORE("https://petstore.swagger.io/v2");

    private final String url;
}
