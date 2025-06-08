package com.svedentsov.db.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.joda.time.DateTime;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.List;

/**
 * Класс для представления роли.
 */
@Data
@Accessors(fluent = true)
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
    private String id;
    private String type;
    private boolean enabled;
    private boolean legacy;
    private int permissionCount;
    private DateTime creationDate;
    private String tenantId;
}
