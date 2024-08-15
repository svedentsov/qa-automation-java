package core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Класс для управления URL-адресами приложения.
 * Предоставляет методы для получения URL-адресов, используемых в приложении.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UrlController {

    /**
     * Основной URL пользовательского интерфейса.
     */
    private static final String MAIN_URL_UI = "the-internet.herokuapp.com";

    /**
     * Возвращает основной URL для доступа к пользовательскому интерфейсу через HTTPS.
     *
     * @return URL для доступа к пользовательскому интерфейсу
     */
    public static String getUiHttpAppHost() {
        return String.format(UrlTemplate.SECURE.getTemplate(), MAIN_URL_UI);
    }

    /**
     * Перечисление шаблонов URL-адресов.
     * Используется для формирования различных типов URL-адресов в приложении.
     */
    @Getter
    @AllArgsConstructor
    private enum UrlTemplate {
        /**
         * Шаблон для безопасного (HTTPS) URL.
         */
        SECURE("https://%s");
        /**
         * Шаблон строки для формирования URL.
         */
        private final String template;
    }
}
