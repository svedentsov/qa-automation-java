package ui.widgets;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import ui.pages.UIRouter;
import org.openqa.selenium.By;

import java.util.ArrayList;

import static com.codeborne.selenide.Selenide.$$;

/**
 * Класс предоставляет методы для проверки наличия, отсутствия и взаимодействия с вариантами.
 */
public class Variants extends UIRouter {

    private final By variant = By.cssSelector(".uk-card");

    /**
     * Проверяет, что вариант с указанным заголовком присутствует.
     *
     * @param title заголовок варианта
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter checkVariantIsPresent(String title) {
        $$(variant).filterBy(Condition.text(title)).shouldBe(CollectionCondition.size(1));
        return this;
    }

    /**
     * Проверяет, что все варианты из списка присутствуют.
     *
     * @param list список заголовков вариантов
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter checkAllVariantsArePresent(ArrayList<String> list) {
        list.forEach(title -> $$(variant).filterBy(Condition.text(title)).shouldBe(CollectionCondition.size(1)));
        return this;
    }

    /**
     * Проверяет, что вариант с указанным заголовком отсутствует.
     *
     * @param title заголовок варианта
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter checkVariantIsNotPresent(String title) {
        $$(variant).filterBy(Condition.text(title)).shouldBe(CollectionCondition.empty);
        return this;
    }

    /**
     * Проверяет, что варианты отсутствуют.
     *
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter checkVariantsEmpty() {
        $$(variant).shouldBe(CollectionCondition.empty);
        return this;
    }

    /**
     * Проверяет количество вариантов.
     *
     * @param count ожидаемое количество вариантов
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter checkVariantsCount(int count) {
        $$(variant).shouldBe(CollectionCondition.size(count));
        return this;
    }

    /**
     * Наводит курсор на вариант с указанным заголовком.
     *
     * @param title заголовок варианта
     * @return экземпляр {@link UIRouter}, представляющий текущую страницу или компонент
     */
    public UIRouter hoverVariant(String title) {
        $$(variant).filterBy(Condition.text(title)).first().hover();
        return this;
    }
}
