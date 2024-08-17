package ui.pages;

import core.annotations.Url;
import org.openqa.selenium.By;
import ui.widgets.Button;
import ui.widgets.LineRead;

/**
 * Класс предоставляет элементы страницы Notification Messages.
 * Содержит кнопку для вызова уведомления и текстовый элемент для проверки сообщения уведомления.
 */
@Url(pattern = ".*/notification_message_rendered")
public class NotificationMessagesPage extends AbstractPage<NotificationMessagesPage> {
    public Button CLICK_HERE_BUTTON = new Button(By.xpath("//a[text()='Click here']"));
    public LineRead NOTIFICATION_MESSAGE_TEXT = new LineRead(By.id("flash"));
}
