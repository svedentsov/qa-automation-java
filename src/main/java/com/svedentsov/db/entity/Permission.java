package com.svedentsov.db.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Embeddable;

/**
 * Встраиваемая сущность Permission - «уровень поглубже».
 */
@Data
@Accessors(fluent = true)
@Embeddable
public class Permission {
    /**
     * Имя разрешения (например, "READ", "WRITE" и т. п.).
     */
    private String name;
    /**
     * Описание разрешения.
     */
    private String description;
}
