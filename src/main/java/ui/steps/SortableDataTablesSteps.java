package ui.steps;

import io.qameta.allure.Step;

/**
 * Класс SortableDataTablesSteps предоставляет шаги для взаимодействия с таблицами на странице с сортируемыми данными.
 * Предоставляет методы для проверки наличия записей в таблице, отсутствия записей и проверки значений полей для конкретных записей.
 */
public class SortableDataTablesSteps extends BaseSteps {

    /**
     * Проверяет наличие записи в указанном столбце таблицы.
     *
     * @param columnName название столбца, в котором выполняется поиск
     * @param recordName название записи, которую необходимо найти
     * @return экземпляр SortableDataTablesSteps для цепочки вызовов
     */
    @Step("Проверить наличие записи в таблице в столбце '{columnName}' для записи '{recordName}'")
    public SortableDataTablesSteps checkRecordIsPresentUsersTable(String columnName, String recordName) {
        ui.sortableDataTablesPage().TABLE_1.checkRecordIsPresent(columnName, recordName);
        return this;
    }

    /**
     * Проверяет отсутствие записи в указанном столбце таблицы.
     *
     * @param columnName название столбца, в котором выполняется поиск
     * @param recordName название записи, которую необходимо проверить на отсутствие
     * @return экземпляр SortableDataTablesSteps для цепочки вызовов
     */
    @Step("Проверить отсутствие записи в таблице в столбце '{columnName}' для записи '{recordName}'")
    public SortableDataTablesSteps checkRecordIsNotPresentUsersTable(String columnName, String recordName) {
        ui.sortableDataTablesPage().TABLE_1.checkRecordIsNotPresent(columnName, recordName);
        return this;
    }

    /**
     * Проверяет значение из указанного поля в таблице для определенной записи.
     *
     * @param columnName         название столбца, в котором выполняется поиск записи
     * @param recordName         название записи, для которой проверяется значение поля
     * @param requiredColumnName название столбца, из которого необходимо получить значение
     * @param expectedText       ожидаемый текст, который должен быть в указанном поле
     * @return экземпляр SortableDataTablesSteps для цепочки вызовов
     */
    @Step("Проверить значение поля '{requiredColumnName}' для записи '{recordName}' в столбце '{columnName}'")
    public SortableDataTablesSteps checkInfoFromFieldUsersTable(String columnName, String recordName, String requiredColumnName, String expectedText) {
        ui.sortableDataTablesPage().TABLE_1.checkInfoFromField(columnName, recordName, requiredColumnName, expectedText);
        return this;
    }
}
