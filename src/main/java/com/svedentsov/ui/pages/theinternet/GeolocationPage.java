package com.svedentsov.ui.pages.theinternet;

import com.svedentsov.core.annotations.Url;
import org.openqa.selenium.By;
import com.svedentsov.ui.element.Button;
import com.svedentsov.ui.element.LineRead;

/**
 * Класс предоставляет элементы страницы Geolocation.
 * Содержит кнопку для получения геолокации и текстовые элементы для отображения координат.
 */
@Url(pattern = ".*/geolocation")
public class GeolocationPage extends AbstractPage<GeolocationPage> {
    public Button GET_LOCATION_BUTTON = new Button(By.xpath("//button[text()='Where am I?']"));
    public LineRead LATITUDE_TEXT = new LineRead(By.id("lat-value"));
    public LineRead LONGITUDE_TEXT = new LineRead(By.id("long-value"));
}
