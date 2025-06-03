package com.svedentsov.db.entity;

import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.List;

/**
 * Класс для представления роли.
 */
@Data
@Embeddable
public class Role {
    /**
     * Имя роли.
     */
    private String name;
    /**
     * Описание роли.
     */
    private String description;
    /**
     * Список «прав» (Permission) у каждой роли.
     */
    @ElementCollection
    private List<Permission> permissions;
}
