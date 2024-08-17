package ui.widgets;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.helper.Widget;

import java.util.stream.IntStream;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.$;

/**
 * Класс предоставляет методы для взаимодействия с таблицами, позволяя проверять наличие записей, получать информацию из полей таблицы и выбирать строки.
 */
public class Table extends Widget<Table> {

    private final By headers = By.tagName("th");
    private final By rows = By.tagName("tr");
    private final By fields = By.tagName("td");

    /**
     * Конструирует объект Table с указанным локатором таблицы.
     *
     * @param locator локатор By для элемента таблицы
     */
    public Table(By locator) {
        super(locator);
    }

    /**
     * Получает номер указанного столбца по его названию в таблице.
     *
     * @param columnName название столбца
     * @return номер столбца; возвращает -1, если столбец не найден
     */
    public int getNumberOfColumn(String columnName) {
        ensureTableIsNotEmpty();
        ElementsCollection headerColumns = $(locator).$$(headers);
        return IntStream.range(0, headerColumns.size())
                .filter(i -> headerColumns.get(i).getText().equalsIgnoreCase(columnName))
                .findFirst()
                .orElse(-1) + 1;
    }

    /**
     * Проверяет наличие записи в указанном столбце таблицы.
     *
     * @param columnName название столбца
     * @param recordName название записи для проверки
     * @return текущий объект Table для цепочки вызовов
     */
    public Table checkRecordIsPresent(String columnName, String recordName) {
        $(locator).$x(getCellXPath(columnName, recordName)).should(Condition.exist);
        return this;
    }

    /**
     * Проверяет отсутствие записи в указанном столбце таблицы.
     *
     * @param columnName название столбца
     * @param recordName название записи для проверки
     * @return текущий объект Table для цепочки вызовов
     */
    public Table checkRecordIsNotPresent(String columnName, String recordName) {
        ensureTableIsNotEmpty();

        String cellXPath = getCellXPath(columnName, recordName);
        try {
            $(locator).$x(cellXPath).shouldNot(Condition.exist);
        } catch (org.openqa.selenium.NoSuchElementException ignored) {
            // Игнорируем NoSuchElementException, так как это ожидаемо, если элемент не найден
        }
        return this;
    }

    /**
     * Получает информацию из указанного поля таблицы для записи.
     *
     * @param columnName     название столбца, содержащего запись
     * @param recordName     название записи
     * @param requiredColumn название столбца, из которого извлекается информация
     * @return информация из указанного поля
     */
    public String getInfoFromField(String columnName, String recordName, String requiredColumn) {
        return $(locator).$x(getCellXPath(columnName, recordName) + "/../td[position()=" + getNumberOfColumn(requiredColumn) + "]").getText();
    }

    /**
     * Проверяет, соответствует ли информация из указанного поля таблицы ожидаемому значению.
     *
     * @param columnName     название столбца, содержащего запись
     * @param recordName     название записи
     * @param requiredColumn название столбца, из которого извлекается информация
     * @param expected       ожидаемое значение
     * @return текущий объект Table для цепочки вызовов
     */
    public Table checkInfoFromField(String columnName, String recordName, String requiredColumn, String expected) {
        SelenideElement required = $(locator).$x(getCellXPath(columnName, recordName) + "/../td[position()=" + getNumberOfColumn(requiredColumn) + "]");
        if (expected.isEmpty()) {
            required.shouldHave(Condition.exactText(""));
        } else {
            required.shouldHave(Condition.text(expected));
        }
        return this;
    }

    /**
     * Выбирает строку таблицы по её номеру.
     *
     * @param rowNumber номер строки в таблице (начиная с 1)
     * @return текущий объект Table для цепочки вызовов
     */
    public Table selectRowByNumber(int rowNumber) {
        ensureTableIsNotEmpty();
        ElementsCollection tableRows = $(locator).$$(rows);
        if (rowNumber > 0 && rowNumber <= tableRows.size()) {
            tableRows.get(rowNumber - 1).click();
        }
        return this;
    }

    /**
     * Выбирает строку таблицы по названию записи в указанном столбце.
     *
     * @param columnName название столбца, содержащего запись
     * @param recordName название записи
     * @return текущий объект Table для цепочки вызовов
     */
    public Table selectRowByName(String columnName, String recordName) {
        ensureTableIsNotEmpty();
        String cellXPath = getCellXPath(columnName, recordName);
        $(locator).$x(cellXPath + "/../td[1]").click(); // Предполагаем, что первая ячейка в строке содержит информацию для выбора строки
        return this;
    }

    /**
     * Проверяет, соответствуют ли заголовки таблицы ожидаемым значениям.
     *
     * @param expectedHeaders массив строк с ожидаемыми значениями заголовков
     * @return текущий объект Table для цепочки вызовов
     */
    public Table checkTableHeaders(String... expectedHeaders) {
        ensureTableIsNotEmpty();
        ElementsCollection actualHeaders = $(locator).$$(headers);
        IntStream.range(0, expectedHeaders.length).forEach(i -> actualHeaders.get(i).shouldHave(Condition.exactText(expectedHeaders[i])));
        return this;
    }

    /**
     * Проверяет, что таблица не пуста. Если таблица пуста, генерирует исключение.
     *
     * @return текущий объект Table для цепочки вызовов
     */
    private Table ensureTableIsNotEmpty() {
        $(locator).$$(rows).shouldBe(sizeGreaterThan(0));
        return this;
    }

    /**
     * Возвращает XPath выражение для ячейки таблицы с указанным именем столбца и записи.
     *
     * @param columnName название столбца
     * @param recordName название записи
     * @return XPath выражение для ячейки таблицы
     */
    private String getCellXPath(String columnName, String recordName) {
        return ".//td[position()=" + getNumberOfColumn(columnName) + " and normalize-space(.)='" + recordName + "']";
    }
}
