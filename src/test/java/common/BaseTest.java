package common;

import rest.AppPetStore;
import ui.AppTheInternet;

/**
 * Базовый класс для всех тестов, содержащий общие объекты для работы с приложениями PetStore и TheInternet.
 */
public abstract class BaseTest {
    /**
     * Экземпляр приложения PetStore для работы с API магазина питомцев.
     */
    protected AppPetStore petStore = AppPetStore.getPetStore();
    /**
     * Экземпляр приложения TheInternet для работы с UI тестами.
     */
    protected AppTheInternet theInternet = AppTheInternet.getTheInternet();
}
