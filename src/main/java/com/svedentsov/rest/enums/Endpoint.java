package com.svedentsov.rest.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Перечисление, представляющее различные конечные точки (эндпоинты) API.
 */
@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public enum Endpoint {

    ORDER("/store/order"),
    ORDER_BY_ID("/store/order/{orderId}"),
    ORDER_INVENTORY("/store/inventory"),

    PET("/pet"),
    PET_ID("/pet/{petId}"),
    PET_FIND_BY_STATUS("/pet/findByStatus"),
    PET_FIND_BY_TAGS("/pet/findByTags"),
    PET_UPLOAD_IMAGE("/pet/{petId}/uploadImage"),

    USER("/user"),
    USER_BY_USERNAME("/user/{username}"),
    USER_CREATE_WITH_ARRAY("/user/createWithArray"),
    USER_CREATE_WITH_LIST("/user/createWithList"),
    USER_LOGIN("/user/login"),
    USER_LOGOUT("/user/logout");

    private final String path;
}

