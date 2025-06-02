package com.svedentsov.steps.theinternet;

import com.svedentsov.manager.UiManager;

/**
 * Базовый класс шагов, предоставляющий доступ к маршрутизатору пользовательского интерфейса.
 */
public class BaseSteps {
    /**
     * Экземпляр маршрутизатора пользовательского интерфейса,
     * используемый для навигации и выполнения действий в пользовательском интерфейсе.
     */
    protected UiManager ui = UiManager.getManager();
}
