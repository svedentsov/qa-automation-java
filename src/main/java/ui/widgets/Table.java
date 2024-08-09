package ui.widgets;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ui.pages.UIRouter;
import org.openqa.selenium.By;

import java.util.stream.IntStream;

import static com.codeborne.selenide.Selenide.$;

/**
 * Класс предоставляет методы для взаимодействия с таблицами.
 */
public class Table extends UIRouter {

    private final By table;
    private final By headers = By.tagName("th");
    private final By rows = By.tagName("tr");
    private final By fields = By.tagName("td");

    /**
     * Конструирует объект Table с указанным локатором таблицы.
     *
     * @param tableLocator локатор By для элемента таблицы
     */
    public Table(By tableLocator) {
        this.table = tableLocator;
    }

    /**
     * Получает номер указанного столбца по его названию в таблице.
     *
     * @param columnName название столбца
     * @return номер столбца; возвращает -1, если столбец не найден
     */
    public int getNumberOfColumn(String columnName) {
        ensureTableIsNotEmpty();
        ElementsCollection headerColumns = $(table).$$(headers);
        return IntStream.range(0, headerColumns.size())
                .filter(i -> headerColumns.get(i).getText().equalsIgnoreCase(columnName))
                .findFirst()
                .orElse(-1) + 1;
    }

    /**
     * Проверяет, присутствует ли указанная сущность в указанном столбце таблицы.
     *
     * @param columnName название столбца.
     * @param entityName название сущности для проверк.
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент.
     */
    public UIRouter checkEntityIsPresent(String columnName, String entityName) {
        $(table).$x(getCellXPath(columnName, entityName)).should(Condition.exist);
        return this;
    }

    /**
     * Проверяет, отсутствует ли указанная сущность в указанном столбце таблицы.
     *
     * @param columnName название столбца.
     * @param entityName название сущности для проверки.
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент.
     */
    public UIRouter checkEntityIsNotPresent(String columnName, String entityName) {
        ensureTableIsNotEmpty();

        String cellXPath = getCellXPath(columnName, entityName);
        try {
            $(table).$x(cellXPath).shouldNot(Condition.exist);
        } catch (org.openqa.selenium.NoSuchElementException ignored) {
            // Игнорируем NoSuchElementException, так как это ожидаемо, если элемент не найден
        }
        return this;
    }


    /**
     * Получает информацию из указанного поля таблицы для указанной сущности.
     *
     * @param columnName     название столбца, содержащего сущность.
     * @param entityName     название сущности.
     * @param requiredColumn название столбца, из которого извлекается информация.
     * @return информация из указанного поля.
     */
    public String getInfoFromField(String columnName, String entityName, String requiredColumn) {
        return $(table).$x(getCellXPath(columnName, entityName) + "/../td[position()=" + getNumberOfColumn(requiredColumn) + "]").getText();
    }

    /**
     * Проверяет, соответствует ли информация из указанного поля таблицы для указанной сущности ожидаемому значению.
     *
     * @param columnName     название столбца, содержащего сущность.
     * @param entityName     название сущности.
     * @param requiredColumn название столбца, из которого извлекается информация.
     * @param expected       ожидаемое значение.
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент.
     */
    public UIRouter checkInfoFromField(String columnName, String entityName, String requiredColumn, String expected) {
        SelenideElement required = $(table).$x(getCellXPath(columnName, entityName) + "/../td[position()=" + getNumberOfColumn(requiredColumn) + "]");
        if (expected.isEmpty())
            required.shouldHave(Condition.exactText(""));
        else
            required.shouldHave(Condition.text(expected));
        return this;
    }

    /**
     * Выбирает строку таблицы по её номеру.
     *
     * @param rowNumber номер строки в таблице (начиная с 1).
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент.
     */
    public UIRouter selectRowByNumber(int rowNumber) {
        ensureTableIsNotEmpty();
        ElementsCollection tableRows = $(table).$$(rows);
        if (rowNumber > 0 && rowNumber <= tableRows.size()) {
            tableRows.get(rowNumber - 1).click();
        }
        return this;
    }

    /**
     * Выбирает строку таблицы по имени сущности в указанном столбце.
     *
     * @param columnName название столбца, содержащего сущность.
     * @param entityName название сущности.
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент.
     */
    public UIRouter selectRowByName(String columnName, String entityName) {
        ensureTableIsNotEmpty();
        String cellXPath = getCellXPath(columnName, entityName);
        $(table).$x(cellXPath + "/../td[1]").click(); // Предполагаем, что первая ячейка в строке содержит информацию для выбора строки
        return this;
    }

    /**
     * Проверяет соответствие заголовков таблицы ожидаемым значениям.
     *
     * @param expectedHeaders массив строк с ожидаемыми значениями заголовков.
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент.
     */
    public UIRouter checkTableHeaders(String... expectedHeaders) {
        ensureTableIsNotEmpty();
        ElementsCollection actualHeaders = $(table).$$(headers);
        IntStream.range(0, expectedHeaders.length).forEach(i -> actualHeaders.get(i).shouldHave(Condition.exactText(expectedHeaders[i])));
        return this;
    }

    /**
     * Проверяет, что таблица не пуста.
     * Если таблица пуста, генерирует исключение.
     */
    private void ensureTableIsNotEmpty() {
        $(table).$$(rows).shouldBe(CollectionCondition.sizeGreaterThan(0));
    }

    /**
     * Возвращает XPath выражение для ячейки таблицы с указанным именем столбца и сущности.
     *
     * @param columnName название столбца.
     * @param entityName название сущности.
     * @return XPath выражение для ячейки таблицы.
     */
    private String getCellXPath(String columnName, String entityName) {
        return ".//td[position()=" + getNumberOfColumn(columnName) + " and normalize-space(.)='" + entityName + "']";
    }
}
