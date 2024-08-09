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
public final class StringUtil {

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
    private static final String FORBIDDEN_XML_10_CHARS_PATTERN = "[^" + "\u0009\r\n" + "\u0020-\uD7FF" + "\uE000-\uFFFD" + "\ud800\udc00-\udbff\udfff" + "]";

    public static String replaceNaAsEmpty(String str) {
        return N_A.equalsIgnoreCase(str) ? StringUtil.EMPTY : str;
    }

    /**
     * Удаляет пробельные символы из строки.
     *
     * @param str Исходная строка.
     * @return Строка без пробельных символов.
     */
    public static String removeWhitespaceChars(String str) {
        return str.replaceAll("\\s+", StringUtil.EMPTY);
    }

    /**
     * Заменяет символ перевода строки на пробел.
     *
     * @param str Исходная строка.
     * @return Строка с замененными символами перевода строки.
     */
    public static String replaceLFtoSpace(String str) {
        return str.replaceAll(LF, StringUtil.SPACE);
    }

    /**
     * Удаляет запрещенные символы XML 1.0 из строки.
     *
     * @param str Исходная строка.
     * @return Строка без запрещенных символов XML 1.0.
     */
    public static String removeForbiddenXmlChars(String str) {
        return str.replaceAll(FORBIDDEN_XML_10_CHARS_PATTERN, StringUtil.EMPTY);
    }

    /**
     * Извлекает цифры из строки.
     *
     * @param str Исходная строка.
     * @return Строка, содержащая только цифры.
     */
    public static String getDigits(String str) {
        return str.replaceAll("[^0-9]", StringUtil.EMPTY);
    }

    /**
     * Получает целое число из строки.
     *
     * @param str Исходная строка.
     * @return Целое число.
     */
    public static int getNumberFromStr(String str) {
        return Integer.parseInt(getDigits(str));
    }

    /**
     * Получает вещественное число из строки.
     *
     * @param str Исходная строка.
     * @return Вещественное число.
     */
    public static double getDoubleFromStr(String str) {
        return Double.parseDouble(getDigitsAndDots(str));
    }

    /**
     * Извлекает цифры и точки из строки.
     *
     * @param str Исходная строка.
     * @return Строка, содержащая только цифры и точки.
     */
    public static String getDigitsAndDots(String str) {
        return str.replaceAll("[^0-9.]", StringUtil.EMPTY);
    }

    /**
     * Извлекает буквы из строки.
     *
     * @param str Исходная строка.
     * @return Строка, содержащая только буквы.
     */
    public static String getChars(String str) {
        return str.replaceAll("[^a-z,A-Z]", StringUtil.EMPTY);
    }

    /**
     * Получает первое совпадение с регулярным выражением в тексте.
     *
     * @param regex Регулярное выражение.
     * @param text  Текст для поиска.
     * @return Первое совпадение или {@code null}, если совпадений нет.
     */
    public static String getFirstMatch(String regex, String text) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        return matcher.find() ? matcher.group(0) : null;
    }

    /**
     * Получает все совпадения с регулярным выражением в тексте.
     *
     * @param regex Регулярное выражение.
     * @param text  Текст для поиска.
     * @return Список строк с совпадениями.
     */
    public static List<String> getAllMatches(String regex, String text) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group().trim());
        }
        return list;
    }

    /**
     * Удаляет нули после точки в строке с числом.
     *
     * @param text Строка с числом.
     * @return Строка без нулей после точки.
     */
    public static String trimZeros(String text) {
        return text.replaceAll("\\.00", EMPTY);
    }

    /**
     * Конвертирует строку в вещественное число.
     *
     * @param value Строка с числом.
     * @return Вещественное число или 0.0, если конвертация не удалась.
     */
    public static Double convertToDouble(String value) {
        return Optional.ofNullable(StringUtil.getFirstMatch("\\d+(\\.\\d+)", value)).map(Double::valueOf).orElse(0.0);
    }

    /**
     * Удаляет последний символ, если он присутствует.
     *
     * @param result Результат.
     * @param symbol Символ для удаления.
     * @return Результат без последнего символа, если он совпадает с заданным символом.
     */
    public static String deleteLastSymbolIfPresent(String result, String symbol) {
        if (result.endsWith(symbol)) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
