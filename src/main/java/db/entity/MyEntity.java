package db.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Сущность MyEntity для демонстрации валидации.
 */
@Data
@Accessors(fluent = true)
@Entity
@Table(name = "my_entity")
@NamedQueries({
        @NamedQuery(
                name = "MyEntity.findByStatus",
                query = "FROM MyEntity WHERE status = :status"
        )
})
public class MyEntity {
    /**
     * Идентификатор сущности.
     */
    @Id
    private String id;
    /**
     * Имя сущности.
     */
    private String name;
    /**
     * Статус сущности.
     */
    private String status;
    /**
     * Тип сущности.
     */
    private String type;
    /**
     * Роль (как строка).
     */
    private String role;
    /**
     * Описание сущности.
     */
    private String description;
    /**
     * Email сущности.
     */
    private String email;
    /**
     * Отчество сущности.
     */
    private String middleName;
    /**
     * Возраст сущности.
     */
    private Integer age;
    /**
     * Баллы или рейтинг сущности.
     */
    private BigDecimal score;
    /**
     * Список ролей в виде строк.
     */
    @ElementCollection
    private List<String> roles;
    /**
     * Дата создания сущности.
     */
    private LocalDateTime creationDate;
    /**
     * Дополнительное поле с Optional.
     */
    @Transient
    private Optional<String> middleNameOptional;
    /**
     * Вложенный объект с адресом.
     */
    @Embedded
    private Address address;
    /**
     * Коллекция сущностей ролей.
     */
    @ElementCollection
    private List<Role> roleEntities;
}

/**
 * Встраиваемый класс для хранения адреса.
 */
@Data
@Embeddable
class Address {
    /**
     * Город.
     */
    private String city;
    /**
     * Улица.
     */
    private String street;
}

/**
 * Класс для представления роли.
 */
@Data
class Role {
    /**
     * Имя роли.
     */
    private String name;
    /**
     * Описание роли.
     */
    private String description;
}
