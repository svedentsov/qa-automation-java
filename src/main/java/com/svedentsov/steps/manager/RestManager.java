package com.svedentsov.steps.manager;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import com.svedentsov.rest.service.PetService;
import com.svedentsov.rest.service.StoreService;
import com.svedentsov.rest.service.UserService;
import com.svedentsov.steps.common.RestSteps;

import java.util.Optional;

/**
 * Менеджер сервисов для работы с API, предоставляющий доступ к различным сервисам API, таким как {@link PetService},
 * {@link StoreService} и {@link UserService}.
 * Этот класс реализует паттерн Singleton, чтобы обеспечить единственный экземпляр менеджера сервисов в приложении.
 * Используется для получения экземпляров сервисов, которые взаимодействуют с API.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestManager {

    private static RestManager restManager;
    private PetService petService;
    private StoreService storeService;
    private UserService userService;
    private RestSteps restSteps;

    /**
     * Получение единственного экземпляра {@link RestManager}.
     *
     * @return единственный экземпляр {@link RestManager}
     */
    public synchronized static RestManager getManager() {
        return Optional.ofNullable(restManager).orElseGet(() -> restManager = new RestManager());
    }

    public RestSteps steps() {
        return Optional.ofNullable(restSteps).orElseGet(() -> restSteps = new RestSteps());
    }

    /**
     * Получение экземпляра {@link PetService}.
     *
     * @return экземпляр {@link PetService}
     */
    public PetService petService() {
        return Optional.ofNullable(petService).orElseGet(() -> petService = new PetService());
    }

    /**
     * Получение экземпляра {@link StoreService}.
     *
     * @return экземпляр {@link StoreService}
     */
    public StoreService storeService() {
        return Optional.ofNullable(storeService).orElseGet(() -> storeService = new StoreService());
    }

    /**
     * Получение экземпляра {@link UserService}.
     *
     * @return экземпляр {@link UserService}
     */
    public UserService userService() {
        return Optional.ofNullable(userService).orElseGet(() -> userService = new UserService());
    }
}
