package com.svedentsov.db.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Встраиваемый класс для хранения адреса.
 * Используется как компонент в других сущностях, таких как {@link MyEntity}.
 */
@Data
@Accessors(fluent = true)
@Embeddable
public class Address {
    /**
     * Город.
     */
    private String city;
    /**
     * Улица.
     */
    private String street;
}
