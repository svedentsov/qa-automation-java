package common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Вспомогательный класс для работы с объектами JavaBean, включая копирование свойств, создание объектов и работу с полями.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanTools {
    /**
     * Копирует свойства из одного объекта в другой.
     *
     * @param dest объект, в который будут скопированы свойства
     * @param orig объект, из которого будут скопированы свойства
     * @throws IllegalStateException если произошла ошибка доступа или ошибка вызова метода
     */
    public static void copyProperties(Object dest, Object orig) {
        try {
            BeanUtils.copyProperties(dest, orig);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Ошибка IllegalAccessException при копировании полей объекта", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Ошибка InvocationTargetException при копировании полей объекта", e);
        }
    }

    /**
     * Конвертирует один объект в другой, создавая новый экземпляр целевого типа.
     *
     * @param <T>  тип целевого объекта
     * @param <F>  тип исходного объекта
     * @param from исходный объект, который необходимо конвертировать
     * @param to   класс целевого объекта
     * @return новый объект целевого типа с копированными свойствами
     * @throws IllegalStateException если произошла ошибка при создании объекта или копировании свойств
     */
    public static <T, F> T convert(F from, Class<T> to) {
        T t;
        try {
            t = to.getDeclaredConstructor().newInstance();
            BeanTools.copyProperties(t, from);
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка при конвертации объекта: " + e.getMessage(), e);
        }

        return t;
    }

    /**
     * Проверяет, содержит ли объект поле с указанным именем.
     *
     * @param object    объект для проверки
     * @param fieldName имя поля
     * @return true, если поле с таким именем существует, иначе false
     */
    public static boolean doesObjectContainField(Object object, String fieldName) {
        if (object == null) {
            return false;
        }
        return Arrays.stream(object.getClass().getDeclaredFields())
                .anyMatch(f -> f.getName().equals(fieldName));
    }

    /**
     * Возвращает значение поля объекта в виде строки.
     *
     * @param field  поле, значение которого нужно получить
     * @param object объект, содержащий поле
     * @return значение поля в виде строки, или null в случае ошибки
     */
    public static String getFieldValueAsString(Field field, Object object) {
        field.setAccessible(true);
        try {
            return (String) field.get(object);
        } catch (IllegalAccessException e) {
            log.trace("Не удалось получить значение поля {}: {}", field.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * Возвращает значение поля объекта.
     *
     * @param field  поле, значение которого нужно получить
     * @param object объект, содержащий поле
     * @return значение поля или null в случае ошибки
     */
    public static Object getFieldValue(Field field, Object object) {
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            log.trace("Не удалось получить поле {}: {}", field.getName(), e.getMessage());
            return null;
        }
    }

    /**
     * Возвращает объект Field по его имени.
     *
     * @param fieldName имя поля
     * @param object    объект, содержащий поле
     * @return объект Field или null в случае отсутствия поля
     */
    public static Field getFieldByName(String fieldName, Object object) {
        try {
            return object.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            log.trace("Не удалось найти поле {}: {}", fieldName, e.getMessage());
            return null;
        }
    }

    /**
     * Создает новый объект с использованием указанного класса и конструктора с параметром.
     *
     * @param <T>                  тип создаваемого объекта
     * @param <C>                  тип параметра конструктора
     * @param objectClass          класс создаваемого объекта
     * @param constructorClass     класс параметра конструктора
     * @param constructorParameter параметр конструктора
     * @return созданный объект
     * @throws IllegalArgumentException если не удается создать объект
     */
    public static <T, C> T createObjectWithConstructorParam(Class<T> objectClass, Class<C> constructorClass, C constructorParameter) {
        try {
            return objectClass.getDeclaredConstructor(constructorClass).newInstance(constructorParameter);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalArgumentException("Невозможно создать объект класса " + objectClass + ": " + e.getMessage(), e);
        }
    }

    /**
     * Создает новый объект с использованием указанного класса и конструктора без параметров.
     *
     * @param <T>         тип создаваемого объекта
     * @param objectClass класс создаваемого объекта
     * @return созданный объект
     * @throws IllegalArgumentException если не удается создать объект
     */
    public static <T> T createObject(Class<T> objectClass) {
        try {
            return objectClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalArgumentException("Невозможно создать объект класса " + objectClass, e);
        }
    }

    /**
     * Возвращает значение поля объекта, обернутое в Optional.
     *
     * @param field  поле, значение которого нужно получить
     * @param object объект, содержащий поле
     * @return Optional с объектом, содержащим значение поля
     */
    public static Optional<Object> getFieldValueAsOptional(Field field, Object object) {
        field.setAccessible(true);
        try {
            return Optional.ofNullable(field.get(object));
        } catch (IllegalAccessException e) {
            log.trace("Не удалось получить значение поля {}: {}", field.getName(), e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Возвращает внутренний объект, представленный полем с указанным именем, обернутый в Optional.
     *
     * @param object    объект, содержащий поле
     * @param fieldName имя поля
     * @return Optional с объектом, содержащим внутренний объект
     */
    public static Optional<Object> getInnerObject(Object object, String fieldName) {
        if (doesObjectContainField(object, fieldName)) {
            Field field = getFieldByName(fieldName, object);
            return Optional.ofNullable(getFieldValue(field, object));
        }
        return Optional.empty();
    }

    /**
     * Возвращает список статических констант указанного класса.
     *
     * @param <T>   тип констант
     * @param clazz класс, содержащий константы
     * @return список статических констант
     */
    public static <T> List<T> getStaticConstants(Class<T> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<T> results = new ArrayList<>();

        for (var field : declaredFields) {
            if (field.getType() == clazz) {
                T obj = convertStaticFieldToObject(field);
                results.add(obj);
            }
        }

        return results;
    }

    /**
     * Конвертирует статическое поле в объект.
     *
     * @param <T>   тип объекта
     * @param field поле, которое нужно конвертировать
     * @return объект, представляющий значение поля
     * @throws IllegalStateException если произошла ошибка доступа к полю
     */
    private static <T> T convertStaticFieldToObject(Field field) {
        try {
            return (T) field.get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Не удалось конвертировать поле в объект", e);
        }
    }
}
