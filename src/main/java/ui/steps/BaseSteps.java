package ui.steps;

import ui.helper.PageManager;

/**
 * Базовый класс шагов, предоставляющий доступ к маршрутизатору пользовательского интерфейса.
 */
public class BaseSteps {

    /**
     * Экземпляр маршрутизатора пользовательского интерфейса,
     * используемый для навигации и выполнения действий в пользовательском интерфейсе.
     */
    public PageManager pages = PageManager.getPageManager();
}
