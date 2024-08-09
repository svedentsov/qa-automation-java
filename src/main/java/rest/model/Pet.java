package rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import rest.enums.PetStatus;

import java.util.List;

/**
 * Модель объекта Pet (питомец).
 */
@Data
@Accessors(fluent = true)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Pet {
    /**
     * Уникальный идентификатор питомца.
     */
    @JsonProperty("id")
    private long id;
    /**
     * Категория питомца.
     */
    @JsonProperty("category")
    private Category category;
    /**
     * Имя питомца.
     */
    @JsonProperty("name")
    private String name;
    /**
     * Список URL-адресов фотографий питомца.
     */
    @JsonProperty("photoUrls")
    private List<String> photoUrls;
    /**
     * Список тегов питомца.
     */
    @JsonProperty("tags")
    private List<Tag> tags;
    /**
     * Статус питомца.
     */
    @JsonProperty("status")
    private PetStatus status;
}
