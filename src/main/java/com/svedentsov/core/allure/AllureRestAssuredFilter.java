package com.svedentsov.core.allure;

import io.qameta.allure.restassured.AllureRestAssured;

/**
 * Класс AllureRestAssuredFilter предоставляет методы для настройки логирования HTTP-запросов и ответов
 * с использованием библиотеки AllureRestAssured.
 */
public class AllureRestAssuredFilter {

    private static final AllureRestAssured FILTER = new AllureRestAssured();

    /**
     * Метод для настройки пользовательских шаблонов логирования запросов и ответов.
     *
     * @return экземпляр AllureRestAssured с настроенными шаблонами
     */
    public static AllureRestAssured withCustomTemplates() {
        FILTER.setRequestTemplate("request.ftl"); // Установка пользовательского шаблона для логирования запросов
        FILTER.setResponseTemplate("response.ftl"); // Установка пользовательского шаблона для логирования ответов
        return FILTER;
    }
}
