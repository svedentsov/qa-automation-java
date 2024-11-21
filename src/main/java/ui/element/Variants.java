package ui.element;

import org.openqa.selenium.By;
import ui.helper.Widget;

import java.util.ArrayList;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Класс предоставляет методы для проверки наличия, отсутствия и взаимодействия с вариантами.
 */
public class Variants extends Widget<Variants> {

    public Variants() {
        super(By.cssSelector(".uk-card"));
    }

    /**
     * Проверяет, что вариант с указанным заголовком присутствует.
     *
     * @param title заголовок варианта
     */
    public Variants checkVariantIsPresent(String title) {
        $$(locator).filterBy(text(title)).shouldBe(size(1));
        return this;
    }

    /**
     * Проверяет, что все варианты из списка присутствуют.
     *
     * @param list список заголовков вариантов
     */
    public Variants checkAllVariantsArePresent(ArrayList<String> list) {
        list.forEach(title -> $$(locator).filterBy(text(title)).shouldBe(size(1)));
        return this;
    }

    /**
     * Проверяет, что вариант с указанным заголовком отсутствует.
     *
     * @param title заголовок варианта
     */
    public Variants checkVariantIsNotPresent(String title) {
        $$(locator).filterBy(text(title)).shouldBe(empty);
        return this;
    }

    /**
     * Проверяет, что варианты отсутствуют.
     */
    public Variants checkVariantsEmpty() {
        $$(locator).shouldBe(empty);
        return this;
    }

    /**
     * Проверяет количество вариантов.
     *
     * @param count ожидаемое количество вариантов
     */
    public Variants checkVariantsCount(int count) {
        $$(locator).shouldBe(size(count));
        return this;
    }

    /**
     * Наводит курсор на вариант с указанным заголовком.
     *
     * @param title заголовок варианта
     */
    public Variants hoverVariant(String title) {
        $$(locator).filterBy(text(title)).first().hover();
        return this;
    }
}
