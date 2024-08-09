package docs;

import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * Класс для тестирования вложений в Allure.
 */
public class AllureAttachmentTests {

    /**
     * Тест с аннотированным вложением.
     */
    @Test
    public void annotatedAttachmentTest() {
        textAttachment("Аннотированное", "hello, world!");
    }

    /**
     * Тест с динамическим вложением.
     */
    @Test
    public void dynamicAttachmentTest() {
        Allure.attachment("Динамическое вложение", "содержание вложения");
    }

    /**
     * Метод для создания текстового вложения в Allure.
     *
     * @param type    Тип вложения, используется в названии вложения.
     * @param content Содержимое вложения.
     * @return Возвращает содержимое вложения в виде массива байт.
     */
    @Attachment(value = "Аннотированное вложение [{type}]", type = "text/plain", fileExtension = ".txt")
    public byte[] textAttachment(String type, String content) {
        return content.getBytes(StandardCharsets.UTF_8);
    }
}
