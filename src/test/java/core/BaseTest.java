package core;

import com.svedentsov.steps.PetStoreSteps;
import com.svedentsov.steps.TheInternetSteps;

/**
 * Базовый класс для всех тестов, содержащий общие объекты для работы с приложениями PetStore и TheInternet.
 */
public abstract class BaseTest {
    /**
     * Экземпляр приложения PetStore для работы с API магазина питомцев.
     */
    protected PetStoreSteps petStore = PetStoreSteps.getPetStore();
    /**
     * Экземпляр приложения TheInternet для работы с UI тестами.
     */
    protected TheInternetSteps theInternet = TheInternetSteps.getTheInternet();
}
