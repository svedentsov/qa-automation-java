package com.svedentsov.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Утилиты для работы со Stream API (StreamUtils)")
class StreamUtilsTest {

    @Nested
    @DisplayName("Методы преобразования (map)")
    class MapTests {

        @Test
        @DisplayName("Должен преобразовывать элементы Stream")
        void shouldMapStream() {
            Stream<Integer> inputStream = Stream.of(1, 2, 3);
            List<String> result = StreamUtils.map(inputStream, String::valueOf);
            assertEquals(List.of("1", "2", "3"), result);
        }

        @Test
        @DisplayName("Должен преобразовывать элементы Collection")
        void shouldMapCollection() {
            Collection<String> inputCollection = List.of("java", "test", "stream");
            List<Integer> result = StreamUtils.map(inputCollection, String::length);
            assertEquals(List.of(4, 4, 6), result);
        }

        @Test
        @DisplayName("Должен преобразовывать элементы Array")
        void shouldMapArray() {
            String[] inputArray = {"a", "b", "c"};
            List<String> result = StreamUtils.map(inputArray, s -> s + s);
            assertEquals(List.of("aa", "bb", "cc"), result);
        }

        @Test
        @DisplayName("Должен преобразовывать элементы и оставлять только уникальные")
        void shouldMapAndDistinct() {
            Collection<Integer> inputCollection = List.of(1, 2, 2, 3, 3, 3);
            List<String> result = StreamUtils.mapAndDistinct(inputCollection, i -> "val-" + i);
            assertEquals(List.of("val-1", "val-2", "val-3"), result);
        }

        @Test
        @DisplayName("Должен преобразовывать Enum в List")
        void shouldMapEnumToList() {
            List<String> result = StreamUtils.mapEnumToList(TestEnum.class, Enum::name);
            assertEquals(List.of("A", "B"), result);
        }

        enum TestEnum {
            A, B
        }
    }

    @Nested
    @DisplayName("Методы фильтрации (filter)")
    class FilterTests {

        @Test
        @DisplayName("Должен фильтровать элементы Stream")
        void shouldFilterStream() {
            Stream<Integer> inputStream = Stream.of(1, 2, 3, 4, 5);
            List<Integer> result = StreamUtils.filter(inputStream, i -> i % 2 == 0);
            assertEquals(List.of(2, 4), result);
        }

        @Test
        @DisplayName("Должен фильтровать элементы Collection")
        void shouldFilterCollection() {
            Collection<String> inputCollection = List.of("apple", "banana", "kiwi", "grape");
            List<String> result = StreamUtils.filter(inputCollection, s -> s.length() > 5);
            assertEquals(List.of("banana"), result);
        }

        @Test
        @DisplayName("Должен фильтровать элементы Array")
        void shouldFilterArray() {
            Integer[] inputArray = {-1, 0, 1, -2, 2};
            List<Integer> result = StreamUtils.filter(inputArray, i -> i < 0);
            assertEquals(List.of(-1, -2), result);
        }

        @Test
        @DisplayName("Должен фильтровать элементы и оставлять только уникальные")
        void shouldFilterAndDistinct() {
            Collection<Integer> inputCollection = List.of(1, 2, 2, 3, 4, 4, 5);
            List<Integer> result = StreamUtils.filterAndDistinct(inputCollection, i -> i % 2 != 0);
            assertEquals(List.of(1, 3, 5), result);
        }
    }

    @Nested
    @DisplayName("Методы поиска первого элемента")
    class GetFirstTests {

        @Test
        @DisplayName("Должен находить первый элемент в Collection")
        void shouldGetFirstFromCollection() {
            Collection<Integer> collection = List.of(1, 10, 2, 20);
            Integer result = StreamUtils.getFirst(collection, i -> i > 15, "big number");
            assertEquals(20, result);
        }

        @Test
        @DisplayName("Должен выбрасывать исключение, если элемент в Collection не найден")
        void shouldThrowWhenFirstNotFoundInCollection() {
            Collection<Integer> collection = List.of(1, 10, 2, 20);
            assertThrows(NoSuchElementException.class, () ->
                    StreamUtils.getFirst(collection, i -> i > 100, "very big number")
            );
        }

        @Test
        @DisplayName("Должен находить первый элемент в Set")
        void shouldGetFirstFromSet() {
            Set<String> set = Set.of("one", "two", "three");
            String result = StreamUtils.getFirst(set, s -> s.startsWith("t"), "t-word");
            // Порядок в Set не гарантирован, поэтому проверяем, что найден один из подходящих
            assertTrue(result.equals("two") || result.equals("three"));
        }

        @Test
        @DisplayName("Должен находить первый элемент в Array")
        void shouldGetFirstFromArray() {
            String[] array = {"A", "B", "C"};
            String result = StreamUtils.getFirst(array, "B"::equals, "letter B");
            assertEquals("B", result);
        }

        @Test
        @DisplayName("Должен находить первый элемент в List и возвращать Optional")
        void shouldGetFirstInOptionalWhenPresent() {
            List<String> list = List.of("a", "b", "c");
            Optional<String> result = StreamUtils.getFirstInOptional(list, "b"::equals);
            assertTrue(result.isPresent());
            assertEquals("b", result.get());
        }

        @Test
        @DisplayName("Должен возвращать пустой Optional, если элемент в Array не найден")
        void shouldReturnEmptyOptionalWhenNotFound() {
            String[] array = {"a", "b", "c"};
            Optional<String> result = StreamUtils.getFirstInOptional(array, "d"::equals);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Должен находить первую константу в Enum")
        void shouldGetFirstEnum() {
            TestEnum result = StreamUtils.getFirstEnum(TestEnum.class, e -> e.name().startsWith("A"), "A-status");
            assertEquals(TestEnum.ACTIVE, result);
        }

        @Test
        @DisplayName("Должен выбрасывать исключение, если константа в Enum не найдена")
        void shouldThrowWhenEnumNotFound() {
            assertThrows(NoSuchElementException.class, () ->
                    StreamUtils.getFirstEnum(TestEnum.class, e -> e.name().equals("DELETED"), "DELETED")
            );
        }

        enum TestEnum {
            ACTIVE, INACTIVE
        }
    }

    @Nested
    @DisplayName("Утилиты для коллекций")
    class CollectionUtilsTests {
        @Test
        @DisplayName("Должен удалять дубликаты из Collection")
        void shouldReturnDistinct() {
            Collection<Integer> input = List.of(1, 2, 2, 3, 1, 4);
            List<Integer> result = StreamUtils.distinct(input);
            // Порядок может измениться, поэтому сравниваем содержимое
            assertEquals(4, result.size());
            assertTrue(result.containsAll(List.of(1, 2, 3, 4)));
        }

        @Test
        @DisplayName("Должен находить пересечение двух коллекций")
        void shouldReturnIntersection() {
            Collection<Integer> initial = List.of(1, 2, 3, 4);
            Collection<Integer> forComparison = List.of(3, 4, 5, 6);
            List<Integer> result = StreamUtils.intersect(initial, forComparison);
            assertEquals(List.of(3, 4), result);
        }

        @Test
        @DisplayName("Должен возвращать пустой список при пересечении с пустой коллекцией")
        void shouldReturnEmptyListOnIntersectWithEmpty() {
            Collection<Integer> initial = List.of(1, 2, 3);
            Collection<Integer> forComparison = List.of();
            List<Integer> result = StreamUtils.intersect(initial, forComparison);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Утилиты генерации и объединения")
    class GenerationTests {

        @Test
        @DisplayName("Должен генерировать последовательность чисел")
        void shouldIterate() {
            List<Integer> result = StreamUtils.iterate(5).collect(Collectors.toList());
            assertEquals(List.of(0, 1, 2, 3, 4), result);
        }

        @Test
        @DisplayName("Должен объединять несколько потоков")
        void shouldConcatStreams() {
            Stream<String> s1 = Stream.of("a", "b");
            Stream<String> s2 = Stream.of("c", "d");
            Stream<String> s3 = Stream.of("e");
            List<String> result = StreamUtils.concat(List.of(s1, s2, s3)).collect(Collectors.toList());
            assertEquals(List.of("a", "b", "c", "d", "e"), result);
        }

        @Test
        @DisplayName("Должен возвращать пустой поток при объединении пустого списка")
        void shouldReturnEmptyStreamWhenConcatEmptyList() {
            List<Stream<Object>> emptyList = List.of();
            long count = StreamUtils.concat(emptyList).count();
            assertEquals(0, count);
        }
    }

    @Nested
    @DisplayName("Параллельное выполнение задач")
    class ParallelExecutionTests {

        @Test
        @DisplayName("Должен выполнять разные задачи параллельно и собирать результаты")
        void shouldExecuteTasksInParallel() {
            Supplier<Object> task1 = () -> "first";
            Supplier<Object> task2 = () -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) { /* игнорируем */ }
                return "second";
            };
            Supplier<Object> task3 = () -> 3;

            // Теперь метод возвращает List<Object>, а не List<String>
            List<Object> results = StreamUtils.executeTasksInParallel(task1, task2, task3);

            assertEquals(3, results.size());
            assertTrue(results.containsAll(List.of("first", "second", 3)));
        }

        @Test
        @DisplayName("Должен выполнять одну и ту же задачу параллельно заданное количество раз")
        void shouldExecuteSameTaskInParallel() {
            final AtomicInteger counter = new AtomicInteger(0);
            Supplier<Integer> task = counter::incrementAndGet;
            int amount = 5;
            // Метод executeSameTaskInParallel ничего не возвращает,
            // но мы можем проверить побочный эффект
            StreamUtils.executeSameTaskInParallel(task, amount);
            assertEquals(amount, counter.get());
        }
    }
}
