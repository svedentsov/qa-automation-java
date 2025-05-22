package rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Модель объекта User (пользователь).
 */
@Data
@Accessors(fluent = true)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /**
     * Уникальный идентификатор пользователя.
     */
    @JsonProperty("id")
    private Long id;
    /**
     * Имя пользователя.
     */
    @JsonProperty("username")
    private String username;
    /**
     * Имя пользователя.
     */
    @JsonProperty("firstName")
    private String firstName;
    /**
     * Фамилия пользователя.
     */
    @JsonProperty("lastName")
    private String lastName;
    /**
     * Email пользователя.
     */
    @JsonProperty("email")
    private String email;
    /**
     * Пароль пользователя.
     */
    @JsonProperty("password")
    private String password;
    /**
     * Номер телефона пользователя.
     */
    @JsonProperty("phone")
    private String phone;
    /**
     * Статус пользователя.
     */
    @JsonProperty("userStatus")
    private Integer userStatus;
}
