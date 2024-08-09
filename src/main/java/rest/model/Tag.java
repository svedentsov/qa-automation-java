package rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Класс, представляющий тег.
 */
@Data
@Accessors(fluent = true)
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    /**
     * Идентификатор тега.
     */
    @JsonProperty("id")
    private long id;
    /**
     * Название тега.
     */
    @JsonProperty("name")
    private String name;
}
