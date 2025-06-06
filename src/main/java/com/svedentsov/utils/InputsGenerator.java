package com.svedentsov.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InputsGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final String ruChars = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private static final String latChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String space = " ";
    private static final String specChars = "«♣☺♂»,«»‘~!@#$%^&*()?>,./\\][/*!—«»,«${}»;—";
    private static final int defaultLength = 77;
    private static final int shortStringLength = 8;

    /**
     * Получает случайное значение из перечисления.
     *
     * @param clazz класс перечисления
     * @param <T>   тип перечисления
     * @return случайное значение из перечисления
     */
    public static <T extends Enum<?>> T getRandomFromEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    /**
     * Получает случайный элемент из списка.
     *
     * @param givenList список
     * @param <T>       тип элементов списка
     * @return случайный элемент из списка
     */
    public static <T> T getRandomFromList(List<T> givenList) {
        return givenList.get(random.nextInt(givenList.size()));
    }

    /**
     * Получает случайное булево значение.
     *
     * @return случайное булево значение
     */
    public static boolean getRandomBool() {
        return random.nextBoolean();
    }

    /**
     * Генерирует строку из кириллических символов с пробелами с длиной по умолчанию.
     *
     * @return строка из кириллических символов с пробелами с длиной по умолчанию
     */
    public static String getCyrillicStrWithSpaces() {
        return getCyrillicStrWithSpaces(defaultLength);
    }

    /**
     * Генерирует строку из латинских символов с пробелами с длиной по умолчанию.
     *
     * @return строка из латинских символов с пробелами с длиной по умолчанию
     */
    public static String getLatinStrWithSpaces() {
        return getLatinStrWithSpaces(defaultLength);
    }

    /**
     * Генерирует строку из специальных символов с пробелами с длиной по умолчанию.
     *
     * @return строка из специальных символов с пробелами с длиной по умолчанию
     */
    public static String getSpecCharStrWithSpaces() {
        return getSpecCharStrWithSpaces(defaultLength);
    }

    /**
     * Получает инъекцию JavaScript.
     *
     * @return инъекция JavaScript
     */
    public static String getJSInjection() {
        return "alert( 'HI' );";
    }

    /**
     * Получает инъекцию SQL.
     *
     * @return инъекция SQL
     */
    public static String getSQLInjection() {
        return "ы'); DROP TABLE Person;--";
    }

    /**
     * Генерирует случайный адрес электронной почты.
     *
     * @return случайный адрес электронной почты
     */
    public static String getRandomEmail() {
        return getShortLatinStr() + "@" + getShortLatinStr() + ".ru";
    }

    /**
     * Генерирует случайный IP-адрес.
     *
     * @return случайный IP-адрес
     */
    public static String getRandomIP() {
        return "" + getRandomIntInRange(1, 254) + "." + getRandomIntInRange(0, 255) + "." + getRandomIntInRange(0, 255) + "." + getRandomIntInRange(0, 255);
    }

    /**
     * Генерирует случайный IP-адрес с портом.
     *
     * @return случайный IP-адрес с портом
     */
    public static String getRandomIPWithPort() {
        return getRandomIP() + ":" + getRandomIntInRange(1, 65535);
    }

    /**
     * Генерирует случайное значение широты.
     *
     * @return случайное значение широты
     */
    public static String getLatitude() {
        return removeTrailingZeroes("59" + "." + getRandomIntInRange(50000, 62000));
    }

    /**
     * Генерирует случайное значение долготы.
     *
     * @return случайное значение долготы
     */
    public static String getLongitude() {
        return removeTrailingZeroes("30" + "." + getRandomIntInRange(19000, 40000));
    }

    /**
     * Удаляет ведущие нули из строки.
     *
     * @param s исходная строка
     * @return строка без ведущих нулей
     */
    private static String removeLeadingZeroes(String s) {
        return StringUtils.stripStart(s, "0");
    }

    /**
     * Удаляет завершающие нули из строки.
     *
     * @param s исходная строка
     * @return строка без завершающих нулей
     */
    private static String removeTrailingZeroes(String s) {
        return StringUtils.stripEnd(s, "0");
    }

    /**
     * Генерирует случайное целое число в заданном диапазоне.
     *
     * @param min минимальное значение
     * @param max максимальное значение
     * @return случайное целое число
     */
    public static int getRandomIntInRange(int min, int max) {
        if (min > max)
            throw new IllegalArgumentException("max must be greater than min");
        else if (max == min)
            return min;
        return random.nextInt((max - min) + 1) + min;
    }

    /**
     * Генерирует случайное длинное целое число в заданном диапазоне.
     *
     * @param min минимальное значение
     * @param max максимальное значение
     * @return случайное длинное целое число
     */
    public static Long getRandomLongInRange(int min, int max) {
        if (min > max)
            throw new IllegalArgumentException("max must be greater than min");
        else if (max == min)
            return (long) min;
        return (long) (random.nextInt((max - min) + 1) + min);
    }

    /**
     * Получает инъекцию HTML.
     *
     * @return инъекция HTML
     */
    public static String getHTMLInjection() {
        return "<table border='1'>" +
                " <caption>Таблица размеров обуви</caption>" +
                " <tr>" +
                " <th>Россия</th>" +
                " <th>Великобритания</th>" +
                " <th>Европа</th>" +
                " <th>Длина ступни, см</th>" +
                " </tr>" +
                " </table>";
    }

    /**
     * Получает временную метку.
     *
     * @return временная метка
     */
    public static String getTimestamp() {
        return (new SimpleDateFormat("YYMMddHHmmss")).format(new Date());
    }

    /**
     * Преобразует число в строку.
     *
     * @param from начальное значение диапазона чисел
     * @param to   конечное значение диапазона чисел
     * @return строковое представление числа
     */
    public static String getNumAsStr(int from, int to) {
        return Integer.toString(random.nextInt((to - from) + 1) + from);
    }

    /**
     * Генерирует строку из латинских символов без пробелов заданной длины.
     *
     * @param count длина строки
     * @return строка из латинских символов без пробелов заданной длины
     */
    public static String getLatinStrWithoutSpaces(int count) {
        return RandomStringUtils.random(count, latChars);
    }

    /**
     * Генерирует короткую строку из латинских символов без пробелов.
     *
     * @return короткая строка из латинских символов без пробелов
     */
    public static String getShortLatinStr() {
        return getLatinStrWithoutSpaces(shortStringLength);
    }

    /**
     * Генерирует строку из латинских символов без пробелов с длиной по-умолчанию.
     *
     * @return строка из латинских символов без пробелов с длиной по-умолчанию
     */
    public static String getLatinStrWithoutSpaces() {
        return RandomStringUtils.random(defaultLength);
    }

    /**
     * Генерирует строку из латинских символов с пробелами заданной длины
     *
     * @param length длина строки
     * @return строка из латинских символов с пробелами заданной длины
     */
    public static String getLatinStrWithSpaces(int length) {
        return RandomStringUtils.random(length, latChars + space).replace(" ", " ").trim();
    }

    /**
     * Генерирует строку из русских символов без пробелов заданной длины.
     *
     * @param count длина строки
     * @return строка из русских символов без пробелов заданной длины
     */
    public static String getCyrillicStrWithoutSpaces(int count) {
        return RandomStringUtils.random(count, ruChars);
    }

    /**
     * Генерирует короткую строку из русских символов без пробелов.
     *
     * @return короткая строка из русских символов без пробелов
     */
    public static String getShortCyrillicStr() {
        return getCyrillicStrWithoutSpaces(shortStringLength);
    }

    /**
     * Генерирует строку из русских символов без пробелов с длиной по-умолчанию.
     *
     * @return строка из русских символов без пробелов заданной длины
     */
    public static String getCyrillicStrWithoutSpaces() {
        return getCyrillicStrWithoutSpaces(defaultLength);
    }

    /**
     * Генерирует строку из кириллических символов с пробелами заданной длины.
     *
     * @param length длина строки
     * @return строка из кириллических символов с пробелами заданной длины
     */
    public static String getCyrillicStrWithSpaces(int length) {
        return RandomStringUtils.random(length, ruChars + space).replace(" ", " ").trim();
    }

    /**
     * Генерирует строку из латинских символов с пробелами заданной длины с фиксированным текстом.
     *
     * @param count    длина строки
     * @param testName фиксированный текст
     * @return строка из латинских символов с пробелами заданной длины с фиксированным текстом
     */
    public static String getLatinStrWithSpacesAndTestName(int count, String testName) {
        return (testName.replace(" ", " ").trim() + " " + RandomStringUtils.random(count, latChars).replace(" ", "").trim());
    }

    /**
     * Генерирует строку из специальных символов с пробелами заданной длины.
     *
     * @param count длина строки
     * @return строка из специальных символов с пробелами заданной длины
     */
    public static String getSpecCharStrWithSpaces(int count) {
        return RandomStringUtils.random(count, specChars);
    }
}
