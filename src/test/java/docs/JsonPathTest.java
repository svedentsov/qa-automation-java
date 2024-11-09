package docs;

import com.jayway.jsonpath.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static com.jayway.jsonpath.Filter.filter;

@DisplayName("JsonPath Tests: Использование библиотеки JsonPath для работы с JSON")
public class JsonPathTest {

    private static final String JSON_DATA = "{\n" +
            "  \"store\": {\n" +
            "    \"book\": [\n" +
            "      {\n" +
            "        \"category\": \"reference\",\n" +
            "        \"author\": \"Nigel Rees\",\n" +
            "        \"title\": \"Sayings of the Century\",\n" +
            "        \"price\": 8.95\n" +
            "      },\n" +
            "      {\n" +
            "        \"category\": \"fiction\",\n" +
            "        \"author\": \"Evelyn Waugh\",\n" +
            "        \"title\": \"Sword of Honour\",\n" +
            "        \"price\": 12.99\n" +
            "      },\n" +
            "      {\n" +
            "        \"category\": \"fiction\",\n" +
            "        \"author\": \"Herman Melville\",\n" +
            "        \"title\": \"Moby Dick\",\n" +
            "        \"isbn\": \"0-553-21311-3\",\n" +
            "        \"price\": 8.99\n" +
            "      },\n" +
            "      {\n" +
            "        \"category\": \"fiction\",\n" +
            "        \"author\": \"J. R. R. Tolkien\",\n" +
            "        \"title\": \"The Lord of the Rings\",\n" +
            "        \"isbn\": \"0-395-19395-8\",\n" +
            "        \"price\": 22.99\n" +
            "      }\n" +
            "    ],\n" +
            "    \"bicycle\": {\n" +
            "      \"color\": \"red\",\n" +
            "      \"price\": 19.95\n" +
            "    }\n" +
            "  },\n" +
            "  \"expensive\": 10\n" +
            "}";

    @Test
    @DisplayName("Извлечение значения по пути JsonPath: Имя автора первой книги")
    public void simpleReadExample() {
        String author = JsonPath.read(JSON_DATA, "$.store.book[0].author");
        System.out.println("Автор первой книги: " + author);
    }

    @Test
    @DisplayName("Извлечение списка значений: Все авторы книг в магазине")
    public void readListExample() {
        List<String> authors = JsonPath.read(JSON_DATA, "$.store.book[*].author");
        System.out.println("Авторы книг: " + authors);
    }

    @Test
    @DisplayName("Использование фильтров: Книги с ценой ниже 10$")
    public void filterExample() {
        List<Map<String, Object>> cheapBooks = JsonPath.read(JSON_DATA, "$.store.book[?(@.price < 10)]");
        System.out.println("Книги дешевле 10$: " + cheapBooks);
    }

    @Test
    @DisplayName("Логические фильтры: Книги категории 'fiction' с ценой ниже 10$")
    public void logicalFilterExample() {
        List<Map<String, Object>> filteredBooks = JsonPath.read(JSON_DATA,
                "$.store.book[?(@.category == 'fiction' && @.price < 10)]");
        System.out.println("Фильтрованные книги: " + filteredBooks);
    }

    @Test
    @DisplayName("Проверка существования поля: Книги с полем 'isbn'")
    public void checkFieldExistenceExample() {
        List<Map<String, Object>> booksWithIsbn = JsonPath.read(JSON_DATA, "$.store.book[?(@.isbn)]");
        System.out.println("Книги с ISBN: " + booksWithIsbn);
    }

    @Test
    @DisplayName("Использование функций JsonPath: Получение количества книг")
    public void jsonPathFunctionsExample() {
        Integer bookCount = JsonPath.read(JSON_DATA, "$.store.book.length()");
        System.out.println("Количество книг: " + bookCount);
    }

    @Test
    @DisplayName("Конфигурация JsonPath: Опция ALWAYS_RETURN_LIST для списка результатов")
    public void configurationExample() {
        Configuration conf = Configuration.builder().options(Option.ALWAYS_RETURN_LIST).build();
        List<String> authors = JsonPath.using(conf).parse(JSON_DATA).read("$.store.book[*].author");
        System.out.println("Авторы книг с конфигурацией: " + authors);
    }

    @Test
    @DisplayName("Модификация JSON: Добавление новой книги в список книг")
    public void jsonModificationExample() {
        DocumentContext context = JsonPath.parse(JSON_DATA);
        Map<String, Object> newBook = new HashMap<>();
        newBook.put("category", "programming");
        newBook.put("author", "Joshua Bloch");
        newBook.put("title", "Effective Java");
        newBook.put("price", 45.00);
        context.add("$.store.book", newBook);
        String updatedJson = context.jsonString();
        System.out.println("Обновленный JSON: " + updatedJson);
    }

    @Test
    @DisplayName("Удаление из JSON: Удаление поля 'expensive'")
    public void jsonDeletionExample() {
        DocumentContext context = JsonPath.parse(JSON_DATA);
        context.delete("$.expensive");
        String updatedJson = context.jsonString();
        System.out.println("JSON после удаления: " + updatedJson);
    }

    @Test
    @DisplayName("Использование операторов JsonPath: Демонстрация различных операторов для извлечения данных")
    public void operatorsExample() {
        List<Double> prices = JsonPath.read(JSON_DATA, "$..price");
        System.out.println("Все цены: " + prices);

        Map<String, Object> lastBook = JsonPath.read(JSON_DATA, "$.store.book[-1]");
        System.out.println("Последняя книга: " + lastBook);

        List<Map<String, Object>> firstTwoBooks = JsonPath.read(JSON_DATA, "$.store.book[0,1]");
        System.out.println("Первые две книги: " + firstTwoBooks);

        Object topKeys = JsonPath.read(JSON_DATA, "$.*");
        System.out.println("Верхнеуровневые ключи: " + topKeys);
    }

    @Test
    @DisplayName("Использование регулярных выражений: Книги, авторы которых начинаются на 'J'")
    public void regexExample() {
        List<Map<String, Object>> booksByJAuthors = JsonPath.read(JSON_DATA,
                "$.store.book[?(@.author =~ /J.*/)]");
        System.out.println("Книги авторов на 'J': " + booksByJAuthors);
    }

    @Test
    @DisplayName("Использование предикатов и фильтров: Фильтр сложных условий")
    public void filterWithPredicateExample() {
        Filter cheapFictionFilter = filter(Criteria.where("category").is("fiction").and("price").lte(10.0));
        List<Map<String, Object>> books = JsonPath.parse(JSON_DATA)
                .read("$.store.book[?]", cheapFictionFilter);
        System.out.println("Дешевые художественные книги: " + books);
    }

    @Test
    @DisplayName("Рекурсивный спуск: Извлечение всех авторов независимо от уровня вложенности")
    public void recursiveDescentExample() {
        List<String> authors = JsonPath.read(JSON_DATA, "$..author");
        System.out.println("Все авторы (рекурсивный спуск): " + authors);
    }

    @Test
    @DisplayName("Извлечение данных как Map: Объект 'store'")
    public void readAsMapExample() {
        Map<String, Object> store = JsonPath.parse(JSON_DATA).read("$.store");
        System.out.println("Объект 'store': " + store);
    }

    @Test
    @DisplayName("Использование DEFAULT_PATH_LEAF_TO_NULL: Возвращает null при отсутствии пути")
    public void defaultPathLeafToNullExample() {
        Configuration conf = Configuration.builder().options(Option.DEFAULT_PATH_LEAF_TO_NULL).build();
        List<String> subtitles = JsonPath.using(conf).parse(JSON_DATA).read("$.store.book[*].subtitle");
        System.out.println("Субтитры (с DEFAULT_PATH_LEAF_TO_NULL): " + subtitles);
    }

    @Test
    @DisplayName("Срез массива: Извлечение подмножества элементов массива")
    public void arraySlicingExample() {
        List<Map<String, Object>> books = JsonPath.read(JSON_DATA, "$.store.book[1:3]");
        System.out.println("Книги с индексами от 1 до 2: " + books);
    }

    @Test
    @DisplayName("Работа с JSON-массивами верхнего уровня: Извлечение данных, когда корневой элемент — массив")
    public void topLevelArrayExample() {
        String jsonArray = "[{\"name\":\"John\"}, {\"name\":\"Jane\"}]";
        List<String> names = JsonPath.read(jsonArray, "$[*].name");
        System.out.println("Имена из массива: " + names);
    }

    @Test
    @DisplayName("Обновление значения в JSON: Обновление значения поля 'price' у первой книги")
    public void updateValueExample() {
        DocumentContext context = JsonPath.parse(JSON_DATA);
        context.set("$.store.book[0].price", 9.99);
        String updatedJson = context.jsonString();
        System.out.println("Обновленный JSON: " + updatedJson);
    }

    @Test
    @DisplayName("Рекурсивный поиск по ключу: Извлечение всех значений 'price'")
    public void recursiveKeySearchExample() {
        List<Double> prices = JsonPath.read(JSON_DATA, "$..price");
        System.out.println("Все цены: " + prices);
    }

    @Test
    @DisplayName("Извлечение уникальных значений: Уникальный список авторов книг")
    public void uniqueValuesExample() {
        List<String> authors = JsonPath.read(JSON_DATA, "$.store.book[*].author");
        Set<String> uniqueAuthors = new HashSet<>(authors);
        System.out.println("Уникальные авторы: " + uniqueAuthors);
    }

    @Test
    @DisplayName("Суммирование значений: Общая стоимость всех книг в магазине")
    public void sumValuesExample() {
        List<Double> prices = JsonPath.read(JSON_DATA, "$.store.book[*].price");
        double totalPrice = prices.stream().mapToDouble(Double::doubleValue).sum();
        System.out.println("Общая стоимость книг: " + totalPrice);
    }

    @Test
    @DisplayName("Обработка данных с Java Stream API: Фильтрация книг по категории и извлечение названий")
    public void streamProcessingExample() {
        List<Map<String, Object>> books = JsonPath.read(JSON_DATA, "$.store.book[*]");
        List<String> titles = books.stream()
                .filter(book -> "fiction".equals(book.get("category")))
                .map(book -> (String) book.get("title"))
                .collect(Collectors.toList());
        System.out.println("Названия художественных книг: " + titles);
    }

    @Test
    @DisplayName("Проверка наличия ключа в JSON: Проверка наличия объекта 'bicycle'")
    public void keyExistenceCheckExample() {
        boolean hasBicycle = JsonPath.parse(JSON_DATA).read("$.store.bicycle") != null;
        System.out.println("Наличие велосипеда в магазине: " + hasBicycle);
    }

    @Test
    @DisplayName("Извлечение данных с несколькими условиями: Книги категории 'fiction' с ценой между 10 и 20$")
    public void multipleConditionsExample() {
        List<Map<String, Object>> books = JsonPath.read(JSON_DATA,
                "$.store.book[?(@.category == 'fiction' && @.price >= 10 && @.price <= 20)]");
        System.out.println("Книги по заданным условиям: " + books);
    }

    @Test
    @DisplayName("Обработка чисел с плавающей точкой: Округление цен книг до двух знаков после запятой")
    public void floatingPointHandlingExample() {
        List<Double> prices = JsonPath.read(JSON_DATA, "$.store.book[*].price");
        List<Double> roundedPrices = prices.stream()
                .map(price -> Math.round(price * 100.0) / 100.0)
                .toList();
        System.out.println("Округленные цены: " + roundedPrices);
    }

    @Test
    @DisplayName("Объединение результатов: Автор и категория книг в одном списке")
    public void mergeResultsExample() {
        List<String> authors = JsonPath.read(JSON_DATA, "$.store.book[*].author");
        List<String> categories = JsonPath.read(JSON_DATA, "$.store.book[*].category");
        List<String> combined = new ArrayList<>();
        for (int i = 0; i < authors.size(); i++) {
            combined.add(authors.get(i) + " - " + categories.get(i));
        }
        System.out.println("Автор и категория: " + combined);
    }
}
