package com.svedentsov.db.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сущность MyEntity для демонстрации валидации различных типов данных.
 * Использует Lombok для автоматической генерации boilerplate кода.
 */
@Data // Генерирует геттеры, сеттеры, toString, equals, hashCode
@Accessors(fluent = true) // Позволяет использовать fluent-сеттеры (e.g., entity.name("value"))
@NoArgsConstructor // Генерирует конструктор без аргументов, необходимый для JPA
@Entity // Помечает класс как JPA-сущность
@Table(name = "my_entity") // Указывает имя таблицы в базе данных
@NamedQueries({@NamedQuery(name = "MyEntity.findByStatus", query = "FROM MyEntity WHERE status = :status")})
public class MyEntity {

    /**
     * Уникальный идентификатор сущности.
     */
    @Id // Помечает поле как первичный ключ
    private String id;

    /**
     * Имя сущности.
     */
    private String name;

    /**
     * Статус сущности (например, "ACTIVE", "INACTIVE").
     */
    private String status;

    /**
     * Тип сущности (например, "USER", "PRODUCT").
     */
    private String type;

    /**
     * Роль пользователя или объекта в виде строки.
     */
    private String role;

    /**
     * Подробное описание сущности.
     */
    private String description;

    /**
     * Email адрес сущности.
     */
    private String email;

    /**
     * URL адрес сущности.
     */
    private String url;

    /**
     * Отчество сущности. Может быть null.
     */
    private String middleName;

    /**
     * Строка, которая может быть пустой или содержать только пробелы, для тестов валидации.
     */
    private String blankString;

    /**
     * Строковое представление булева значения (например, "true", "false").
     */
    private String isTrueString;

    /**
     * Строка с уникальными символами для проверок уникальности.
     */
    private String uniqueCharsString;

    /**
     * Многострочный текст для проверок количества строк.
     */
    private String multiLineText;

    /**
     * Номер телефона для валидации формата.
     */
    private String phone;

    /**
     * IP-адрес для валидации формата.
     */
    private String ipAddress;

    /**
     * MAC-адрес для валидации формата.
     */
    private String macAddress;

    /**
     * Время события в строковом формате (например, "HH:mm:ss").
     */
    private String eventTime;

    /**
     * Шестнадцатеричный код цвета (например, "#RRGGBB").
     */
    private String hexColor;

    /**
     * Строка в формате JSON.
     */
    private String jsonString;

    /**
     * Строка в формате XML.
     */
    private String xmlString;

    /**
     * Строка, представляющая валидный идентификатор Java.
     */
    private String javaIdentifier;

    /**
     * Строка без символов табуляции.
     */
    private String noTabsString;

    /**
     * Строка без управляющих символов.
     */
    private String noControlCharsString;

    /**
     * Строка без повторяющихся последовательностей символов.
     */
    private String noRepeatedSequencesString;

    /**
     * Строка без последовательных пробелов.
     */
    private String noConsecutiveSpacesString;

    /**
     * Строка без подряд идущих одинаковых символов.
     */
    private String noConsecutiveDuplicateCharsString;

    /**
     * Строка без подряд идущих букв и цифр.
     */
    private String noConsecutiveLetterDigitString;

    /**
     * Строка со смешанным регистром букв и цифрами.
     */
    private String mixedCaseString;

    /**
     * Строка, содержащая только цифры.
     */
    private String numericString;

    /**
     * Строка, закодированная в Base64.
     */
    private String base64String;

    /**
     * Строка, содержащая специальные символы.
     */
    private String specialCharString;

    /**
     * Панграмма (предложение, содержащее все буквы алфавита).
     */
    private String pangramString;

    /**
     * Строка для проверки соответствия wildcard-шаблону.
     */
    private String wildcardString;

    /**
     * Строка с повторяющейся подстрокой для проверки.
     */
    private String repeatedSubstring;

    /**
     * Строка, содержащая только уникальные слова.
     */
    private String uniqueWordsString;

    /**
     * Строка, содержащая только уникальные слова (без учета регистра).
     */
    private String uniqueWordsIgnoreCaseString;

    /**
     * Строка, содержащая только разрешенные символы.
     */
    private String allowedCharsString;

    /**
     * Строка для проверки уникальных биграмм.
     */
    private String bigramString;

    /**
     * Строка для проверки уникальных триграмм.
     */
    private String trigramString;

    /**
     * Строка для проверки на содержание только разрешенных специальных символов.
     */
    private String allowedSpecialCharsString;

    /**
     * Строка для проверки максимального кода символов (например, ASCII).
     */
    private String asciiString;

    /**
     * Строка для проверки на содержание символов только из определенного Unicode блока (например, греческого).
     */
    private String greekString;

    /**
     * Предложение для проверки наличия/отсутствия целых слов и пунктуации.
     */
    private String sentence;

    /**
     * Строка, содержащая символы из нескольких Unicode блоков (например, латиница и кириллица).
     */
    private String mixedUnicodeString;

    /**
     * Строка для проверки на отсутствие длинных последовательностей одинаковых символов.
     */
    private String longSequenceString;

    /**
     * Строка, которая должна содержать только буквенно-цифровые символы согласно регулярному выражению.
     */
    private String alphanumericString;

    /**
     * Строка для проверки на равное количество заглавных и строчных букв.
     */
    private String mixedCaseBalanceString;

    /**
     * Строка для проверки на точное количество заглавных и строчных букв.
     */
    private String exactCaseString;

    /**
     * Строка для проверки на максимальное количество подряд идущих букв одного регистра.
     */
    private String consecutiveCaseString;

    /**
     * Строка для проверки на определенное количество различных символов.
     */
    private String distinctCharsString;

    /**
     * Строка с несколькими пробелами для проверки максимального количества подряд идущих пробелов.
     */
    private String multipleSpacesString;

    /**
     * Строка-пароль для проверки категорий символов (буквы, цифры, спецсимволы).
     */
    private String passwordString;

    /**
     * Сложный пароль для проверки количества символов из нескольких заданных наборов.
     */
    private String complexPasswordString;

    /**
     * Строка с фиксированной длиной, соответствующая определенному шаблону.
     */
    private String fixedPatternString;

    /**
     * Возраст сущности. Используется для числовых проверок.
     */
    private Integer age;

    /**
     * Баллы или рейтинг сущности. Используется для проверок чисел с плавающей точкой.
     */
    private BigDecimal score;

    /**
     * Список числовых значений (например, очки), для проверок предикатов на коллекциях.
     */
    @ElementCollection // Помечает коллекцию базовых типов или встраиваемых объектов
    private List<Integer> scores;

    /**
     * Момент времени создания записи (Instant).
     * Используется для общих проверок Instant.
     */
    private Instant createdAt;

    /**
     * Момент времени последнего обновления записи (Instant).
     * Используется для общих проверок Instant.
     */
    private Instant updatedAt;

    /**
     * Момент времени последнего входа пользователя (Instant).
     * Используется для общих проверок Instant.
     */
    private Instant lastLogin;

    /**
     * Момент времени будущего события (Instant).
     * Используется для общих проверок Instant.
     */
    private Instant futureEventTime;

    /**
     * Дата и время создания сущности (LocalDateTime).
     * Используется для проверок LocalDateTime.
     */
    private LocalDateTime creationDate; // Переименовано для ясности и соответствия примеру

    /**
     * Дата и время создания сущности (LocalDateTime).
     * Используется для проверок LocalDateTime.
     */
    private LocalDateTime localDateTimeCreated;
    private LocalDate eventDate;

    /**
     * Дата и время последнего обновления сущности (LocalDateTime).
     * Используется для проверок LocalDateTime.
     */
    private LocalDateTime localDateTimeUpdated;

    /**
     * Дата и время начала события (LocalDateTime).
     * Используется для проверок LocalDateTime.
     */
    private LocalDateTime localDateTimeEventStart;

    /**
     * Дата и время окончания события (LocalDateTime).
     * Используется для проверок LocalDateTime.
     */
    private LocalDateTime localDateTimeEventEnd;

    /**
     * Конкретная дата и время для тестовых сценариев (LocalDateTime).
     * Используется для проверок LocalDateTime.
     */
    private LocalDateTime localDateTimeSpecific;

    /**
     * Дата и время в високосном году для проверки високосного года (LocalDateTime).
     * Используется для проверок LocalDateTime.
     */
    private LocalDateTime localDateTimeLeapYear;

    /**
     * Вложенный объект с адресом.
     */
    @Embedded // Встраивает поля класса Address в таблицу MyEntity
    private Address address;

    /**
     * Список строковых ролей. Используется для проверок коллекций строк.
     */
    @ElementCollection
    private List<String> roles;

    /**
     * Коллекция сущностей ролей. Используется для проверок коллекций объектов.
     */
    @ElementCollection
    private List<Role> roleEntities;

    /**
     * Список тегов для проверки на пустоту коллекции.
     */
    @ElementCollection
    private List<String> tags;

    /**
     * Отсортированный список целых чисел для проверки сортировки.
     */
    @ElementCollection
    private List<Integer> sortedList;

    /**
     * Список с уникальными ролями для проверки наличия уникальных элементов.
     */
    @ElementCollection
    private List<String> uniqueRoles;

    /**
     * Карта строковых атрибутов для проверок методов, работающих с Map.
     */
    @ElementCollection
    @CollectionTable(name = "my_entity_attributes", joinColumns = @JoinColumn(name = "entity_id"))
    @MapKeyColumn(name = "attribute_key")
    @Column(name = "attribute_value")
    private Map<String, String> attributes;

    /**
     * Пустая карта для проверки propertyMapIsEmpty.
     */
    @ElementCollection
    @CollectionTable(name = "my_entity_empty_map", joinColumns = @JoinColumn(name = "entity_id"))
    @MapKeyColumn(name = "map_key")
    @Column(name = "map_value")
    private Map<String, String> emptyMap;

    /**
     * Дополнительное поле с Optional.
     *
     * @deprecated Поля с Optional не являются стандартной практикой для JPA-сущностей,
     * так как JPA не имеет прямой поддержки для Optional.
     * Используется здесь исключительно для демонстрации в рамках тестов.
     */
    @Transient // Указывает JPA игнорировать это поле
    @Deprecated // Отмечает поле как устаревшее
    private Optional<String> middleNameOptional;
}
