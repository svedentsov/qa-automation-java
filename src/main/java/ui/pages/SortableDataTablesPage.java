package ui.pages;

import org.openqa.selenium.By;
import ui.widgets.Table;

/**
 * Класс для представления страницы "Sortable Data Tables".
 * Содержит таблицы, которые можно сортировать.
 */
public class SortableDataTablesPage {

    public Table TABLE_1 = new Table(By.id("table1"));
    public Table TABLE_2 = new Table(By.id("table2"));
}
