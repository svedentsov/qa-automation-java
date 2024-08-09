package rest;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import rest.helper.RestManager;
import rest.steps.PetSteps;
import rest.steps.StoreSteps;
import rest.steps.UserSteps;

import java.util.Optional;

/**
 * Синглтон-класс для доступа к шагам API тестирования магазина домашних животных.
 * Этот класс используется для получения экземпляров шагов для различных аспектов API,
 * таких, как работа с питомцами, заказами и пользователями.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppPetStore {

    private static AppPetStore appPetStore;
    private RestManager restManager;
    private PetSteps petSteps;
    private StoreSteps storeSteps;
    private UserSteps userSteps;

    /**
     * Получение единственного экземпляра {@link AppPetStore}.
     *
     * @return единственный экземпляр {@link AppPetStore}
     */
    public synchronized static AppPetStore getPetStore() {
        return Optional.ofNullable(appPetStore).orElseGet(() -> appPetStore = new AppPetStore());
    }

    public RestManager rest() {
        return Optional.ofNullable(restManager).orElseGet(() -> restManager = RestManager.getRestManager());
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
