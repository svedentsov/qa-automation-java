package com.svedentsov.steps;

import com.svedentsov.steps.manager.RestManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import com.svedentsov.steps.petstore.PetSteps;
import com.svedentsov.steps.petstore.StoreSteps;
import com.svedentsov.steps.petstore.UserSteps;

import java.util.Optional;

/**
 * Синглтон-класс для доступа к шагам API тестирования магазина домашних животных.
 * Этот класс используется для получения экземпляров шагов для различных аспектов API,
 * таких, как работа с питомцами, заказами и пользователями.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PetStoreSteps {

    private static PetStoreSteps petStoreSteps;
    private RestManager restManager;
    private PetSteps petSteps;
    private StoreSteps storeSteps;
    private UserSteps userSteps;

    /**
     * Получение единственного экземпляра {@link PetStoreSteps}.
     *
     * @return единственный экземпляр {@link PetStoreSteps}
     */
    public synchronized static PetStoreSteps getPetStore() {
        return Optional.ofNullable(petStoreSteps).orElseGet(() -> petStoreSteps = new PetStoreSteps());
    }

    public RestManager rest() {
        return Optional.ofNullable(restManager).orElseGet(() -> restManager = RestManager.getManager());
    }

    /**
     * Получение экземпляра {@link PetSteps}.
     *
     * @return экземпляр {@link PetSteps} для выполнения шагов, связанных с питомцами
     */
    public PetSteps petSteps() {
        return Optional.ofNullable(petSteps).orElseGet(() -> petSteps = new PetSteps());
    }

    /**
     * Получение экземпляра {@link StoreSteps}.
     *
     * @return экземпляр {@link StoreSteps} для выполнения шагов, связанных с заказами в магазине
     */
    public StoreSteps storeSteps() {
        return Optional.ofNullable(storeSteps).orElseGet(() -> storeSteps = new StoreSteps());
    }

    /**
     * Получение экземпляра {@link UserSteps}.
     *
     * @return экземпляр {@link UserSteps} для выполнения шагов, связанных с пользователями
     */
    public UserSteps userSteps() {
        return Optional.ofNullable(userSteps).orElseGet(() -> userSteps = new UserSteps());
    }
}
