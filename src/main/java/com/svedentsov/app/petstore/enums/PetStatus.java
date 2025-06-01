package com.svedentsov.app.petstore.enums;

/**
 * Перечисление, представляющее возможные статусы питомца.
 */
public enum PetStatus {
    /**
     * Питомец доступен для продажи.
     */
    AVAILABLE,
    /**
     * Питомец недоступен для продажи.
     */
    PENDING,
    /**
     * Питомец уже продан.
     */
    SOLD
}
