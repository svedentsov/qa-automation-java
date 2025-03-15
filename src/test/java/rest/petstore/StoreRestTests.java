package rest.petstore;

import core.RestTest;
import core.annotations.Layer;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rest.data.DataGenerator;
import rest.model.Order;
import rest.model.Pet;

import java.util.Map;

import static io.qameta.allure.SeverityLevel.CRITICAL;
import static io.qameta.allure.SeverityLevel.NORMAL;

@Layer("rest")
@Owner("svedentsov")
@Epic("Регресс магазина питомцев")
@Feature("Заказы магазина")
public class StoreRestTests extends RestTest {

    private final Pet minDataPet = DataGenerator.generateMinDataPet();
    private final Order order = DataGenerator.generateValidOrder();

    @Test
    @Severity(CRITICAL)
    @DisplayName("Удаление заказа по ID")
    @Description("Проверяет успешное удаление заказа по его идентификатору")
    public void deleteOrderTest() {
        // Arrange
        Pet createdPet = petStore.petSteps()
                .createPetSuccessfully(minDataPet);
        Order placedOrder = petStore.storeSteps()
                .placeOrder(order.petId(createdPet.id()));

        // Act
        petStore.storeSteps()
                .deleteOrder(placedOrder.id());

        // Assert
        petStore.storeSteps()
                .getNotFoundOrderById(placedOrder.id());
    }

    @Test
    @Severity(NORMAL)
    @DisplayName("Получение количества питомцев в магазине")
    @Description("Проверяет получение корректного количества питомцев в магазине")
    public void getInventoryTest() {
        // Act
        Map<String, Object> inventory = petStore.storeSteps()
                .getInventory();

        // Assert
        petStore.storeSteps()
                .assertInventoryAvailable(inventory);
    }

    @Test
    @Severity(CRITICAL)
    @DisplayName("Поиск заказа по его идентификатору")
    @Description("Проверяет успешный поиск заказа по его идентификатору")
    public void getOrderByIdTest() {
        // Arrange
        Pet createdPet = petStore.petSteps()
                .createPetSuccessfully(minDataPet);
        Order placedOrder = petStore.storeSteps()
                .placeOrder(order.petId(createdPet.id()));

        // Act
        Order fetchedOrder = petStore.storeSteps()
                .getOrderById(placedOrder.id());

        // Assert
        petStore.storeSteps()
                .assertOrderExists(fetchedOrder)
                .assertOrderIdEquals(fetchedOrder, placedOrder.id());
    }

    @Test
    @Severity(CRITICAL)
    @DisplayName("Оформление заказа на питомца")
    @Description("Проверяет успешное оформление заказа на питомца")
    public void placeOrderTest() {
        // Arrange
        Pet createdPet = petStore.petSteps()
                .createPetSuccessfully(minDataPet);
        Order newOrder = order.petId(createdPet.id());

        // Act
        Order placedOrder = petStore.storeSteps()
                .placeOrder(newOrder);

        // Assert
        petStore.storeSteps()
                .assertOrderExists(placedOrder)
                .assertOrderIdEquals(placedOrder, newOrder.id());
    }
}
