package com.svedentsov.db.entity;

import lombok.Data;

import javax.persistence.Embeddable;

/**
 * Встраиваемый класс для хранения адреса.
 */
@Data
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
