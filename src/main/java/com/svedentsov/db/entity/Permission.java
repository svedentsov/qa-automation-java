package com.svedentsov.db.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.experimental.Accessors;


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
