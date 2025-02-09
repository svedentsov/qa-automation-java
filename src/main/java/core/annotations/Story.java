package core.annotations;

import io.qameta.allure.LabelAnnotation;

import java.lang.annotation.*;

/*
 * Аннотация для Allure TestOps
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@LabelAnnotation(name = "story")
public @interface Story {
    String value();
}
