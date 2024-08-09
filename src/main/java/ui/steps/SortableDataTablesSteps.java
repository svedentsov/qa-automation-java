package ui.steps;

import io.qameta.allure.Step;

/**
 * Класс SortableDataTablesSteps предоставляет шаги для взаимодействия с таблицами, используемыми на странице с сортируемыми данными.
 */
public class SortableDataTablesSteps extends BaseSteps {

    /**
     * Проверяет наличие сущности в указанном столбце таблицы.
     *
     * @param columnName название столбца, в котором выполняется поиск
     * @param entityName название сущности, которую необходимо найти
     * @return экземпляр SortableDataTablesSteps для использования в цепочке вызовов
     */
    @Step("Проверить наличие сущности в таблице")
    public SortableDataTablesSteps checkEntityIsPresentUsersTable(String columnName, String entityName) {
        ui.sortableDataTablesPage().TABLE_1.checkEntityIsPresent(columnName, entityName);
        return this;
    }

    /**
     * Проверяет отсутствие сущности в указанном столбце таблицы.
     *
     * @param columnName название столбца, в котором выполняется поиск
     * @param entityName название сущности, которую необходимо проверить на отсутствие
     * @return экземпляр SortableDataTablesSteps для использования в цепочке вызовов
     */
    @Step("Проверить отсутствие сущности в таблице")
    public SortableDataTablesSteps checkEntityIsNotPresentUsersTable(String columnName, String entityName) {
        ui.sortableDataTablesPage().TABLE_1.checkEntityIsNotPresent(columnName, entityName);
        return this;
    }

    /**
     * Проверяет информацию из указанного поля в таблице для определенной сущности.
     *
     * @param columnName         название столбца, в котором выполняется поиск сущности
     * @param entityName         название сущности, для которой проверяется информация из поля
     * @param requiredColumnName название столбца, из которого необходимо получить информацию
     * @param expectedText       ожидаемый текст, который должен быть в указанном поле
     * @return экземпляр SortableDataTablesSteps для использования в цепочке вызовов
     */
    @Step("Проверить информацию из поля в таблице")
    public SortableDataTablesSteps checkInfoFromFieldUsersTable(String columnName, String entityName, String requiredColumnName, String expectedText) {
        ui.sortableDataTablesPage().TABLE_1.checkInfoFromField(columnName, entityName, requiredColumnName, expectedText);
        return this;
    }
}
