package com.svedentsov.db.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;

/**
 * Встраиваемый класс для хранения адреса.
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
