package common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Утилитарный класс для работы с Java Stream API.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StreamUtils {
    /**
     * Преобразует элементы входного потока с использованием заданной функции.
     *
     * @param stream входной поток элементов
     * @param mapper функция преобразования элементов
     * @param <T>    тип элементов входного потока
     * @param <R>    тип элементов результирующего списка
     * @return список, содержащий результат преобразования элементов
     */
    public static <T, R> List<R> map(Stream<T> stream, Function<T, R> mapper) {
        return stream.map(mapper).collect(toList());
    }

    /**
     * Преобразует элементы коллекции с использованием заданной функции.
     *
     * @param collection входная коллекция элементов
     * @param mapper     функция преобразования элементов
     * @param <T>        тип элементов коллекции
     * @param <R>        тип элементов результирующего списка
     * @return список, содержащий результат преобразования элементов
     */
    public static <T, R> List<R> map(Collection<T> collection, Function<T, R> mapper) {
        return map(collection.stream(), mapper);
    }

    /**
     * Преобразует элементы массива с использованием заданной функции.
     *
     * @param array  массив элементов
     * @param mapper функция преобразования элементов
     * @param <T>    тип элементов массива
     * @param <R>    тип элементов результирующего списка
     * @return список, содержащий результат преобразования элементов
     */
    public static <T, R> List<R> map(T[] array, Function<T, R> mapper) {
        return map(List.of(array), mapper);
    }

    /**
     * Преобразует элементы коллекции с использованием заданной функции и возвращает уникальные элементы.
     *
     * @param collection коллекция элементов
     * @param mapper     функция преобразования элементов
     * @param <T>        тип элементов коллекции
     * @param <R>        тип элементов результирующего списка
     * @return список уникальных элементов, содержащий результат преобразования элементов
     */
    public static <T, R> List<R> mapAndDistinct(Collection<T> collection, Function<T, R> mapper) {
        return distinct(map(collection, mapper));
    }

    /**
     * Преобразует элементы перечисления в список с использованием заданной функции.
     *
     * @param enumData перечисление
     * @param mapper   функция преобразования элементов перечисления
     * @param <T>      тип элементов списка
     * @param <E>      тип элементов перечисления
     * @return список, содержащий результат преобразования элементов перечисления
     */
    public static <T, E extends Enum<E>> List<T> mapEnumToList(Class<E> enumData, Function<E, T> mapper) {
        return Stream.of(enumData.getEnumConstants()).map(mapper).collect(toList());
    }

    /**
     * Фильтрует элементы входного потока с использованием заданного предиката.
     *
     * @param stream    входной поток элементов
     * @param predicate предикат для фильтрации элементов
     * @param <T>       тип элементов входного потока
     * @return список, содержащий отфильтрованные элементы
     */
    public static <T> List<T> filter(Stream<T> stream, Predicate<T> predicate) {
        return stream.filter(predicate).collect(toList());
    }

    /**
     * Фильтрует элементы коллекции с использованием заданного предиката.
     *
     * @param collection коллекция элементов
     * @param predicate  предикат для фильтрации элементов
     * @param <T>        тип элементов коллекции
     * @return список, содержащий отфильтрованные элементы
     */
    public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {
        return filter(collection.stream(), predicate);
    }

    /**
     * Фильтрует элементы массива с использованием заданного предиката.
     *
     * @param array     массив элементов
     * @param predicate предикат для фильтрации элементов
     * @param <T>       тип элементов массива
     * @return список, содержащий отфильтрованные элементы
     */
    public static <T> List<T> filter(T[] array, Predicate<T> predicate) {
        return filter(List.of(array), predicate);
    }

    /**
     * Фильтрует элементы коллекции с использованием заданного предиката и возвращает уникальные элементы.
     *
     * @param collection коллекция элементов
     * @param predicate  предикат для фильтрации элементов
     * @param <T>        тип элементов коллекции
     * @return список уникальных, отфильтрованных элементов
     */
    public static <T> List<T> filterAndDistinct(Collection<T> collection, Predicate<T> predicate) {
        return distinct(filter(collection, predicate));
    }

    /**
     * Возвращает первый элемент входного потока, удовлетворяющий заданному предикату.
     *
     * @param origStream  входной поток элементов
     * @param predicate   предикат для фильтрации элементов
     * @param elementName имя элемента (используется в исключении, если элемент не найден)
     * @param <T>         тип элементов входного потока
     * @return первый элемент, удовлетворяющий предикату
     * @throws NoSuchElementException если элемент не найден
     */
    public static <T> T getFirst(Stream<T> origStream, Predicate<T> predicate, String elementName) {
        return origStream.filter(predicate).findFirst().orElseThrow(() -> new NoSuchElementException("element " + elementName + " wasn't found"));
    }

    /**
     * Возвращает первый элемент списка, удовлетворяющий заданному предикату.
     *
     * @param origList    список элементов
     * @param predicate   предикат для фильтрации элементов
     * @param elementName имя элемента (используется в исключении, если элемент не найден)
     * @param <T>         тип элементов списка
     * @return первый элемент, удовлетворяющий предикату
     * @throws NoSuchElementException если элемент не найден
     */
    public static <T> T getFirst(List<T> origList, Predicate<T> predicate, String elementName) {
        return getFirst(origList.stream(), predicate, elementName);
    }

    /**
     * Возвращает первый элемент множества, удовлетворяющий заданному предикату.
     *
     * @param origSet     множество элементов
     * @param predicate   предикат для фильтрации элементов
     * @param elementName имя элемента (используется в исключении, если элемент не найден)
     * @param <T>         тип элементов множества
     * @return первый элемент, удовлетворяющий предикату
     * @throws NoSuchElementException если элемент не найден
     */
    public static <T> T getFirst(Set<T> origSet, Predicate<T> predicate, String elementName) {
        return getFirst(new ArrayList<T>(origSet), predicate, elementName);
    }

    /**
     * Возвращает первый элемент массива, удовлетворяющий заданному предикату.
     *
     * @param origArray   массив элементов
     * @param predicate   предикат для фильтрации элементов
     * @param elementName имя элемента (используется в исключении, если элемент не найден)
     * @param <T>         тип элементов массива
     * @return первый элемент, удовлетворяющий предикату
     * @throws NoSuchElementException если элемент не найден
     */
    public static <T> T getFirst(T[] origArray, Predicate<T> predicate, String elementName) {
        return getFirst(List.of(origArray), predicate, elementName);
    }

    /**
     * Возвращает первый элемент коллекции, удовлетворяющий заданному предикату.
     *
     * @param origCollection коллекция элементов
     * @param predicate      предикат для фильтрации элементов
     * @param elementName    имя элемента (используется в исключении, если элемент не найден)
     * @param <T>            тип элементов коллекции
     * @return первый элемент, удовлетворяющий предикату
     * @throws NoSuchElementException если элемент не найден
     */
    public static <T> T getFirst(Collection<T> origCollection, Predicate<T> predicate, String elementName) {
        return getFirst(origCollection.stream(), predicate, elementName);
    }

    /**
     * Возвращает первый элемент списка в виде {@code Optional}, удовлетворяющий заданному предикату.
     *
     * @param origList  список элементов
     * @param predicate предикат для фильтрации элементов
     * @param <T>       тип элементов списка
     * @return {@code Optional} с первым элементом, удовлетворяющим предикату, или пустой, если элемент не найден
     */
    public static <T> Optional<T> getFirstInOptional(List<T> origList, Predicate<T> predicate) {
        return origList.stream().filter(predicate).findFirst();
    }

    /**
     * Возвращает первый элемент массива в виде {@code Optional}, удовлетворяющий заданному предикату.
     *
     * @param array     массив элементов
     * @param predicate предикат для фильтрации элементов
     * @param <T>       тип элементов массива
     * @return {@code Optional} с первым элементом, удовлетворяющим предикату, или пустой, если элемент не найден
     */
    public static <T> Optional<T> getFirstInOptional(T[] array, Predicate<T> predicate) {
        return getFirstInOptional(List.of(array), predicate);
    }

    /**
     * Возвращает первый элемент перечисления, удовлетворяющий заданному предикату.
     *
     * @param clazz     класс перечисления
     * @param predicate предикат для фильтрации элементов
     * @param value     значение, используемое в исключении, если элемент не найден
     * @param <T>       тип элементов перечисления
     * @return первый элемент, удовлетворяющий предикату
     * @throws NoSuchElementException если элемент не найден
     */
    public static <T extends Enum<T>> T getFirstEnum(Class<T> clazz, Predicate<T> predicate, String value) {
        return Stream.of(clazz.getEnumConstants())
                .filter(predicate)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Enum not found for: " + value));
    }

    /**
     * Удаляет дубликаты из коллекции.
     *
     * @param collection коллекция элементов
     * @param <T>        тип элементов коллекции
     * @return список, содержащий уникальные элементы
     */
    public static <T> List<T> distinct(Collection<T> collection) {
        return collection.stream().distinct().collect(toList());
    }

    /**
     * Возвращает пересечение двух коллекций.
     *
     * @param initial       первая коллекция
     * @param forComparison вторая коллекция
     * @param <T>           тип элементов коллекций
     * @return список, содержащий элементы, присутствующие в обеих коллекциях
     */
    public static <T> List<T> intersect(Collection<T> initial, Collection<T> forComparison) {
        return initial.stream().filter(forComparison::contains).collect(Collectors.toList());
    }

    /**
     * Создает бесконечный поток целых чисел, начиная с 0 и увеличиваясь на 1 до заданного значения.
     *
     * @param lessThan верхняя граница значений (не включительно)
     * @return бесконечный поток целых чисел
     */
    public static Stream<Integer> iterate(int lessThan) {
        return Stream.iterate(0, i -> i < lessThan, i -> i + 1);
    }

    /**
     * Сцепляет несколько потоков в один.
     *
     * @param streamList список потоков
     * @param <T>        тип элементов потоков
     * @return объединенный поток элементов
     */
    public static <T> Stream<T> concat(List<Stream<T>> streamList) {
        return streamList.stream().reduce(Stream::concat).orElseGet(Stream::empty);
    }

    /**
     * Выполняет задачи в параллельном режиме и возвращает результаты в виде списка.
     *
     * @param tasks список задач в виде Supplier
     * @param <T>   тип результата задачи
     * @return список результатов задач
     */
    @SafeVarargs
    public static <T> List<T> executeTasksInParallel(Supplier<T>... tasks) {
        return executeTasksInParallel(List.of(tasks));
    }

    /**
     * Выполняет задачи в параллельном режиме и возвращает результаты в виде списка.
     *
     * @param tasks список задач в виде Supplier
     * @param <T>   тип результата задачи
     * @return список результатов задач
     */
    public static <T> List<T> executeTasksInParallel(List<Supplier<T>> tasks) {
        return tasks.stream()
                .map(CompletableFuture::supplyAsync).toList()
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    /**
     * Выполняет одну и ту же задачу в параллельном режиме заданное количество раз.
     *
     * @param task   задача в виде Supplier
     * @param amount количество выполнений задачи
     * @param <T>    тип результата задачи
     */
    public static <T> void executeSameTaskInParallel(Supplier<T> task, int amount) {
        List<Supplier<T>> suppliers = StreamUtils.iterate(amount).map(i -> task).collect(toList());
        executeTasksInParallel(suppliers);
    }
}
