package rest.enums;

/**
 * Перечисление, представляющее возможные статусы заказа.
 */
public enum OrderStatus {
    /**
     * Заказ одобрен.
     */
    APPROVED,
    /**
     * Заказ доставлен.
     */
    DELIVERED,
    /**
     * Заказ размещен.
     */
    PLACED
}