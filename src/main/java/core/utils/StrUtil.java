package core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Утилитарный класс для работы со строками.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StrUtil {

    public static final String COMMA = ",";
    public static final String DOT = ".";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String SPACE = " ";
    public static final String UNDERSCORE = "_";
    public static final String EMPTY = "";
    public static final String LF = "\n";
    public static final String CR = "\r";
    public static final String SEMICOLON = ";";
    public static final String COLON = ":";
    public static final String SLASH = "/";
    public static final String PERCENT = "%";
    public static final String HASH = "#";
    public static final String EQUAL_SIGN = "=";
    public static final String LEFT_BRACKET = "[";
    public static final String RIGHT_BRACKET = "]";
    public static final String N_A = "N/A";
    public static final String ELLIPSIS = "...";
    public static final String INFINITY = "∞";
    private static final Pattern FORBIDDEN_XML_10_CHARS_PATTERN = Pattern.compile("[^" + "\u0009\r\n" + "\u0020-\uD7FF" + "\uE000-\uFFFD" + "\ud800\udc00-\udbff\udfff" + "]");
    private static final Pattern DIGITS_PATTERN = Pattern.compile("[^0-9]");
    private static final Pattern DIGITS_AND_DOTS_PATTERN = Pattern.compile("[^0-9.]");
    private static final Pattern CHARS_PATTERN = Pattern.compile("[^a-zA-Z]");
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("\\d+(\\.\\d+)?");

    /**
     * Заменяет строку "N/A" на пустую строку.
     *
     * @param str исходная строка
     * @return строка с заменой "N/A" на пустую строку
     */
    public static String replaceNaWithEmpty(String str) {
        return N_A.equalsIgnoreCase(Optional.ofNullable(str).orElse(EMPTY)) ? EMPTY : str;
    }

    /**
     * Удаляет пробельные символы из строки.
     *
     * @param str исходная строка
     * @return строка без пробельных символов
     */
    public static String removeWhitespaceChars(String str) {
        return Optional.ofNullable(str).orElse(EMPTY).replaceAll("\\s+", EMPTY);
    }

    /**
     * Заменяет символ перевода строки на пробел.
     *
     * @param str исходная строка
     * @return строка с замененными символами перевода строки
     */
    public static String replaceLFtoSpace(String str) {
        return Optional.ofNullable(str).orElse(EMPTY).replaceAll("\n", SPACE);
    }

    /**
     * Удаляет запрещенные символы XML 1.0 из строки.
     *
     * @param str исходная строка
     * @return строка без запрещенных символов XML 1.0
     */
    public static String removeForbiddenXmlChars(String str) {
        return FORBIDDEN_XML_10_CHARS_PATTERN.matcher(Optional.ofNullable(str).orElse(EMPTY)).replaceAll(EMPTY);
    }

    /**
     * Извлекает цифры из строки.
     *
     * @param str исходная строка
     * @return строка, содержащая только цифры
     */
    public static String getDigits(String str) {
        return DIGITS_PATTERN.matcher(Optional.ofNullable(str).orElse(EMPTY)).replaceAll(EMPTY);
    }

    /**
     * Получает целое число из строки.
     *
     * @param str исходная строка
     * @return целое число или 0, если строка не содержит цифр
     */
    public static int getNumberFromStr(String str) {
        String digits = getDigits(str);
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }

    /**
     * Получает вещественное число из строки.
     *
     * @param str исходная строка
     * @return вещественное число или 0.0, если строка не содержит числа
     */
    public static double getDoubleFromStr(String str) {
        String number = getDigitsAndDots(str);
        return number.isEmpty() ? 0.0 : Double.parseDouble(number);
    }

    /**
     * Извлекает цифры и точки из строки.
     *
     * @param str исходная строка
     * @return строка, содержащая только цифры и точки
     */
    public static String getDigitsAndDots(String str) {
        return DIGITS_AND_DOTS_PATTERN.matcher(Optional.ofNullable(str).orElse(EMPTY)).replaceAll(EMPTY);
    }

    /**
     * Извлекает буквы из строки.
     *
     * @param str исходная строка
     * @return строка, содержащая только буквы
     */
    public static String getChars(String str) {
        return CHARS_PATTERN.matcher(Optional.ofNullable(str).orElse(EMPTY)).replaceAll(EMPTY);
    }

    /**
     * Получает первое совпадение с регулярным выражением в тексте.
     *
     * @param regex регулярное выражение
     * @param text  текст для поиска
     * @return первое совпадение или {@code null}, если совпадений нет
     */
    public static String getFirstMatch(String regex, String text) {
        Matcher matcher = Pattern.compile(regex).matcher(Optional.ofNullable(text).orElse(EMPTY));
        return matcher.find() ? matcher.group(0) : null;
    }

    /**
     * Получает все совпадения с регулярным выражением в тексте.
     *
     * @param regex регулярное выражение
     * @param text  текст для поиска
     * @return список строк с совпадениями
     */
    public static List<String> getAllMatches(String regex, String text) {
        Matcher matcher = Pattern.compile(regex).matcher(Optional.ofNullable(text).orElse(EMPTY));
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group().trim());
        }
        return list;
    }

    /**
     * Удаляет нули после точки в строке с числом.
     *
     * @param text строка с числом
     * @return строка без нулей после точки
     */
    public static String trimZeros(String text) {
        return Optional.ofNullable(text).orElse(EMPTY).replaceAll("\\.00", EMPTY);
    }

    /**
     * Конвертирует строку в вещественное число.
     *
     * @param value строка с числом
     * @return вещественное число или 0.0, если конвертация не удалась
     */
    public static Double convertToDouble(String value) {
        return Optional.ofNullable(DOUBLE_PATTERN.matcher(Optional.ofNullable(value).orElse(EMPTY)).find() ? value : null)
                .map(Double::valueOf)
                .orElse(0.0);
    }

    /**
     * Удаляет последний символ, если он присутствует.
     *
     * @param result результат
     * @param symbol символ для удаления
     * @return результат без последнего символа, если он совпадает с заданным символом
     */
    public static String deleteLastSymbolIfPresent(String result, String symbol) {
        return Optional.ofNullable(result)
                .filter(r -> r.endsWith(symbol))
                .map(r -> r.substring(0, r.length() - 1))
                .orElse(result);
    }
}
