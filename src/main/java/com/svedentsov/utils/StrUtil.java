package com.svedentsov.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Утилитарный класс для работы со строками, расширенный и оптимизированный.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StrUtil {

    // --- Константы ---
    public static final String COMMA = ",";
    public static final String DOT = ".";
    public static final String SPACE = " ";
    public static final String EMPTY = "";
    public static final String LF = "\n";
    public static final String SEMICOLON = ";";
    public static final String COLON = ":";
    public static final String N_A = "N/A";
    public static final String ELLIPSIS = "...";

    // --- Паттерны ---
    private static final Pattern FORBIDDEN_XML_CHARS = Pattern.compile("[^" + "\u0009\r\n" + "\u0020-\uD7FF" + "\uE000-\uFFFD" + "\ud800\udc00-\udbff\udfff" + "]");
    private static final Pattern FIRST_NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final Map<String, Pattern> PATTERN_CACHE = new ConcurrentHashMap<>();

    /**
     * Возвращает скомпилированный паттерн из кэша или компилирует и кэширует новый.
     *
     * @param regex регулярное выражение
     * @return скомпилированный {@link Pattern}
     */
    private static Pattern cachedPattern(String regex) {
        return PATTERN_CACHE.computeIfAbsent(regex, Pattern::compile);
    }

    // --- Новые утилитарные методы ---

    /**
     * Проверяет, является ли строка null, пустой ("") или состоит только из пробельных символов.
     *
     * @param str строка для проверки
     * @return {@code true}, если строка "бланковая", иначе {@code false}
     */
    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    /**
     * Проверяет, является ли строка null или пустой ("").
     *
     * @param str строка для проверки
     * @return {@code true}, если строка пустая, иначе {@code false}
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * Возвращает строку по умолчанию, если исходная строка null.
     *
     * @param str        исходная строка
     * @param defaultStr строка по умолчанию
     * @return исходную строку или строку по умолчанию, если исходная - null
     */
    public static String defaultIfNull(String str, String defaultStr) {
        return Objects.requireNonNullElse(str, defaultStr);
    }

    /**
     * Укорачивает строку до заданной максимальной ширины, добавляя в конце многоточие.
     *
     * @param str      строка для укорачивания
     * @param maxWidth максимальная длина результирующей строки (должна быть >= 4)
     * @return укороченная строка или исходная, если ее длина меньше maxWidth
     */
    public static String abbreviate(String str, int maxWidth) {
        if (isEmpty(str) || str.length() <= maxWidth) return str;
        if (maxWidth < ELLIPSIS.length() + 1) {
            throw new IllegalArgumentException("maxWidth must be at least " + (ELLIPSIS.length() + 1));
        }
        return str.substring(0, maxWidth - ELLIPSIS.length()) + ELLIPSIS;
    }

    /**
     * Дополняет строку слева указанным символом до заданной длины.
     *
     * @param str     исходная строка
     * @param size    целевая длина строки
     * @param padChar символ для дополнения
     * @return дополненная строка
     */
    public static String leftPad(String str, int size, char padChar) {
        if (str == null) return null;
        int pads = size - str.length();
        return (pads > 0) ? String.valueOf(padChar).repeat(pads) + str : str;
    }

    /**
     * Делает первую букву строки заглавной.
     *
     * @param str исходная строка
     * @return строка с заглавной первой буквой
     */
    public static String capitalize(String str) {
        if (isBlank(str)) return str;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (!Character.isWhitespace(chars[i])) {
                chars[i] = Character.toTitleCase(chars[i]);
                break;
            }
        }
        return new String(chars);
    }

    /**
     * Заменяет строку "N/A" (без учета регистра) на пустую строку. Корректно обрабатывает null.
     *
     * @param str исходная строка
     * @return обработанная строка или пустая строка, если на входе был null
     */
    public static String replaceNaWithEmpty(String str) {
        return N_A.equalsIgnoreCase(str) ? EMPTY : Objects.toString(str, EMPTY);
    }

    /**
     * Удаляет все пробельные символы из строки.
     *
     * @param str исходная строка
     * @return строка без пробельных символов, или пустая строка если на входе null
     */
    public static String removeWhitespace(String str) {
        return isBlank(str) ? EMPTY : str.replaceAll("\\s+", EMPTY);
    }

    /**
     * Удаляет запрещенные символы XML 1.0 из строки.
     *
     * @param str исходная строка
     * @return строка без запрещенных символов XML 1.0
     */
    public static String removeForbiddenXmlChars(String str) {
        return FORBIDDEN_XML_CHARS.matcher(defaultIfNull(str, EMPTY)).replaceAll(EMPTY);
    }

    /**
     * Находит и возвращает первое число (целое или с плавающей точкой) из строки.
     *
     * @param str исходная строка, например "Price: -199.99 USD"
     * @return {@link Optional}, содержащий найденное число в виде строки, или пустой Optional.
     */
    public static Optional<String> findFirstNumber(String str) {
        if (isBlank(str)) return Optional.empty();
        Matcher matcher = FIRST_NUMBER_PATTERN.matcher(str);
        return matcher.find() ? Optional.of(matcher.group()) : Optional.empty();
    }

    /**
     * Получает первое целое число из строки.
     *
     * @param str исходная строка
     * @return целое число или 0, если число не найдено или произошла ошибка парсинга.
     */
    public static int getNumber(String str) {
        return findFirstNumber(str)
                .map(s -> s.contains(DOT) ? s.substring(0, s.indexOf(DOT)) : s)
                .map(Integer::parseInt)
                .orElse(0);
    }

    /**
     * Получает первое вещественное число (double) из строки.
     *
     * @param str исходная строка
     * @return вещественное число или 0.0, если число не найдено.
     */
    public static double getDoubleFromStr(String str) {
        return findFirstNumber(str)
                .map(Double::parseDouble)
                .orElse(0.0);
    }

    /**
     * Получает первое совпадение с регулярным выражением в тексте. Использует кэширование паттернов.
     *
     * @param regex регулярное выражение
     * @param text  текст для поиска
     * @return {@link Optional} с первым совпадением или пустой Optional.
     */
    public static Optional<String> getFirstMatch(String regex, String text) {
        Matcher matcher = cachedPattern(regex).matcher(defaultIfNull(text, EMPTY));
        return matcher.find() ? Optional.of(matcher.group()) : Optional.empty();
    }

    /**
     * Получает все совпадения с регулярным выражением в тексте. Использует кэширование паттернов.
     *
     * @param regex регулярное выражение
     * @param text  текст для поиска
     * @return список строк с совпадениями (может быть пустым).
     */
    public static List<String> getAllMatches(String regex, String text) {
        Matcher matcher = cachedPattern(regex).matcher(defaultIfNull(text, EMPTY));
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
        return list;
    }

    /**
     * Удаляет ".0" или ".00" и т.д. в конце строки, если это дробная часть из нулей.
     *
     * @param text строка с числом
     * @return строка без нулевой дробной части
     */
    public static String trimTrailingZeros(String text) {
        if (text == null || text.isEmpty()) return text;
        int dot = text.indexOf(DOT);
        if (dot < 0) return text;
        String intPart = text.substring(0, dot);
        String frac = text.substring(dot + 1).replaceFirst("0+$", "");
        return frac.isEmpty() ? intPart : intPart + DOT + frac;
    }
}
