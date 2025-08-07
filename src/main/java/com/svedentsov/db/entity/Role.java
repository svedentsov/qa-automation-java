package com.svedentsov.db.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.List;

/**
 * Встраиваемый класс для представления роли.
 * Может использоваться как часть коллекции в других сущностях.
 */
@Data
@Accessors(fluent = true)
@Embeddable
public class Role {
    /**
     * Идентификатор роли.
     */
    private String id;

    /**
     * Имя роли.
     */
    private String name;

    /**
     * Описание роли.
     */
    private String description;

    /**
     * Тип роли.
     */
    private String type;

    /**
     * Флаг, активна ли роль.
     */
    private boolean enabled;

    /**
     * Флаг, является ли роль устаревшей.
     */
    private boolean legacy;

    /**
     * Количество разрешений у роли.
     */
    private int permissionCount;

    /**
     * Момент времени создания роли.
     */
    private Instant creationDate;

    /**
     * Идентификатор арендатора (tenant), к которому относится роль.
     */
    private String tenantId;

    /**
     * Список разрешений ({@link Permission}), связанных с этой ролью.
     */
    @ElementCollection
    private List<Permission> permissions;
}