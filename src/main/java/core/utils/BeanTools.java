package core.utils;

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
 * Утилитарный класс для работы с JavaBeans.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeanTools {

    /**
     * Копирует свойства из одного объекта в другой.
     *
     * @param dest Целевой объект, в который будут скопированы свойства.
     * @param orig Исходный объект, свойства которого будут скопированы.
     */
    public static void copyProperties(Object dest, Object orig) {
        try {
            BeanUtils.copyProperties(dest, orig);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("IllegalAccessException при копировании свойств объекта", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("InvocationTargetException при копировании свойств объекта", e);
        }
    }

    /**
     * Преобразует объект из одного типа в другой.
     *
     * @param from Объект, который будет преобразован.
     * @param to   Класс, в который будет преобразован объект.
     * @param <T>  Тип результирующего объекта.
     * @param <F>  Тип исходного объекта.
     * @return Новый объект типа {@code T}, содержащий скопированные свойства из исходного объекта.
     */
    public static <T, F> T convert(F from, Class<T> to) {
        T t;
        try {
            t = to.getDeclaredConstructor().newInstance();
            BeanTools.copyProperties(t, from);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }

        return t;
    }

    /**
     * Проверяет, содержит ли объект указанное поле.
     *
     * @param object    Объект, в котором проверяется наличие поля.
     * @param fieldName Название поля.
     * @return {@code true}, если объект содержит указанное поле, иначе {@code false}.
     */
    public static boolean isObjectContainField(Object object, String fieldName) {
        boolean result = false;
        if (object != null) {
            result = Arrays.stream(object.getClass().getDeclaredFields())
                    .anyMatch(f -> f.getName().equals(fieldName));
        }
        return result;
    }

    /**
     * Получает значение поля объекта.
     *
     * @param field  Поле, значение которого требуется получить.
     * @param object Объект, у которого требуется получить значение поля.
     * @return Значение поля объекта или {@code null}, если не удалось получить значение.
     */
    public static String getFieldValue(Field field, Object object) {
        String result = null;
        field.setAccessible(true); //NOSONAR
        try {
            result = (String) field.get(object);
        } catch (IllegalAccessException e) {
            log.trace("Не удалось получить значение поля {}\t{}", field, e.getMessage());
        }

        return result;
    }

    /**
     * Получает значение поля объекта.
     *
     * @param field  Поле, значение которого требуется получить.
     * @param object Объект, у которого требуется получить значение поля.
     * @return Значение поля объекта или {@code null}, если не удалось получить значение.
     */
    public static Object getField(Field field, Object object) {
        field.setAccessible(true); //NOSONAR
        Object result = null;
        try {
            result = field.get(object);
        } catch (IllegalAccessException e) {
            // log.trace("Could not get field {}\t{}", field, e.getMessage());
        }

        return result;
    }

    /**
     * Получает поле объекта по его имени.
     *
     * @param fieldName Имя поля.
     * @param object    Объект, у которого требуется получить поле.
     * @return Поле объекта или {@code null}, если поле не найдено.
     */
    public static Field getField(String fieldName, Object object) {
        Field field = null;
        try {
            field = object.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            // log.trace("Could not get field {}\t{}", fieldName, e.getMessage());
        }

        return field;
    }

    /**
     * Создает объект заданного класса с использованием указанного конструктора.
     *
     * @param objectClass          Класс объекта, который требуется создать.
     * @param constructorClass     Класс параметра конструктора.
     * @param constructorParameter Параметр конструктора.
     * @param <T>                  Тип объекта.
     * @param <C>                  Тип параметра конструктора.
     * @return Созданный объект.
     */
    public static <T, C> T createObject(Class<T> objectClass, Class<C> constructorClass, C constructorParameter) {
        try {
            return objectClass.getDeclaredConstructor(constructorClass).newInstance(constructorParameter);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new IllegalArgumentException("Невозможно создать объект класса " + objectClass + "\n" + e.getMessage(), e);
        }
    }

    /**
     * Создает объект заданного класса.
     *
     * @param objectClass Класс объекта, который требуется создать.
     * @param <T>         Тип объекта.
     * @return Созданный объект.
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
     * Получает значение поля объекта в виде объекта типа {@code Optional}.
     *
     * @param field  Поле, значение которого требуется получить.
     * @param object Объект, у которого требуется получить значение поля.
     * @return Значение поля объекта в виде объекта {@code Optional}.
     */
    public static Optional getFieldValueObject(Field field, Object object) {
        Object result = null;
        field.setAccessible(true); //NOSONAR
        try {
            result = field.get(object);
        } catch (IllegalAccessException e) {
            log.trace("Не удалось получить значение поля {}\t{}", field, e.getMessage());
        }

        return Optional.ofNullable(result);
    }

    /**
     * Получает вложенный объект по его имени.
     *
     * @param object    Объект, у которого требуется получить вложенный объект.
     * @param fieldName Имя поля, содержащего вложенный объект.
     * @return Объект типа {@code Optional}, содержащий вложенный объект, если он существует.
     */
    public static Optional getInnerObject(Object object, String fieldName) {
        if (isObjectContainField(object, fieldName)) {
            Field field = getField(fieldName, object);
            return Optional.ofNullable(getField(field, object));
        }
        return Optional.empty();
    }

    /**
     * Получает статические константы заданного класса.
     *
     * @param clazz Класс, чьи статические константы требуется получить.
     * @param <T>   Тип статических констант.
     * @return Список статических констант заданного класса.
     */
    public static <T> List<T> getStaticConstants(Class<T> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<T> results = new ArrayList<>();

        for (var x : declaredFields) {
            if (x.getType() == clazz) {
                T obj = convertFieldToObject(x);
                results.add(obj);
            }
        }

        return results;
    }

    /**
     * Преобразует поле в объект.
     *
     * @param x   Поле, которое требуется преобразовать в объект.
     * @param <T> Тип объекта.
     * @return Объект, полученный из поля.
     */
    private static <T> T convertFieldToObject(Field x) {
        try {
            return (T) x.get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Невозможно преобразовать поле в объект ", e);
        }
    }
}
