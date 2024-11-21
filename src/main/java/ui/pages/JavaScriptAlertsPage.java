package ui.pages;

import common.annotations.Url;
import org.openqa.selenium.By;
import ui.element.Button;
import ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы JavaScript Alerts.
 * Содержит кнопки для взаимодействия с различными типами JavaScript-уведомлений
 * и текст для проверки сообщений, отображаемых после взаимодействия с уведомлениями.
 */
@Url(pattern = ".*/javascript_alerts")
public class JavaScriptAlertsPage extends AbstractPage<JavaScriptAlertsPage> {
    public Button ALERT_BUTTON = new Button(By.xpath("//button[text()='Click for JS Alert']"));
    public Button CONFIRM_BUTTON = new Button(By.xpath("//button[text()='Click for JS Confirm']"));
    public Button PROMPT_BUTTON = new Button(By.xpath("//button[text()='Click for JS Prompt']"));
    public LineRead RESULT_TEXT = new LineRead(By.id("result"));
}
