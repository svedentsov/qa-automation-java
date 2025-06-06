package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.Table;

/**
 * Класс представляет элементы страницы "Sortable Data Tables".
 * Содержит таблицы, которые можно сортировать.
 */
@Url(pattern = ".*/tables")
public class SortableDataTablesPage extends AbstractPage<SortableDataTablesPage> {
    public Table TABLE_1 = new Table(By.id("table1"));
    public Table TABLE_2 = new Table(By.id("table2"));
}
