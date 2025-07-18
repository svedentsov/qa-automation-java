package com.svedentsov.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Утилиты для работы с JSON")
class JsonUtilsTest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class User {
        private int id;
        private String name;
        private String email;
        private List<String> roles;
        private LocalDate registrationDate;
    }

    private final User testUser = new User(1, "Test User", "test@example.com", List.of("ADMIN", "USER"), LocalDate.of(2025, 7, 18));
    private final String testUserJson = "{\"id\":1,\"name\":\"Test User\",\"email\":\"test@example.com\",\"roles\":[\"ADMIN\",\"USER\"],\"registrationDate\":\"2025-07-18\"}";

    @Test
    @DisplayName("Сериализует POJO в JSON строку, включая дату")
    void shouldSerializePojoToJson() {
        String json = JsonUtils.toJson(testUser);
        assertEquals(testUserJson, json);
    }

    @Test
    @DisplayName("Десериализует JSON строку в POJO, включая дату")
    void shouldDeserializeJsonToPojo() {
        User user = JsonUtils.fromJson(testUserJson, User.class);
        assertEquals(testUser, user);
    }

    @Test
    @DisplayName("Сериализует в красивый JSON с отступами")
    void shouldSerializeToPrettyJson() {
        String prettyJson = JsonUtils.toPrettyJson(Map.of("key", "value"));
        assertTrue(prettyJson.contains(System.lineSeparator()));
        assertTrue(prettyJson.contains("  "));
    }

    @Test
    @DisplayName("Десериализует JSON массив в List с помощью TypeReference")
    void shouldDeserializeJsonToListWithTypeReference() {
        List<User> userList = List.of(testUser);
        String jsonList = JsonUtils.toJson(userList);
        List<User> deserializedList = JsonUtils.fromJson(jsonList, new TypeReference<>() {
        });
        assertEquals(userList, deserializedList);
    }

    @Test
    @DisplayName("Конвертирует POJO в Map")
    void shouldConvertPojoToMap() {
        Map<String, Object> userMap = JsonUtils.convertValue(testUser, new TypeReference<>() {
        });
        assertEquals(1, userMap.get("id"));
        assertEquals("Test User", userMap.get("name"));
        assertEquals("2025-07-18", userMap.get("registrationDate"));
    }

    @Test
    @DisplayName("Обновляет существующий объект из JSON")
    void shouldUpdateValueFromJson() {
        User userToUpdate = new User(1, "Old Name", "old@email.com", List.of(), LocalDate.of(2000, 1, 1));
        String updateJson = "{\"name\":\"New Name\",\"roles\":[\"UPDATED\"]}";

        JsonUtils.updateValue(userToUpdate, updateJson);

        assertEquals(1, userToUpdate.getId()); // ID не изменился
        assertEquals("New Name", userToUpdate.getName()); // Имя обновилось
        assertEquals("old@email.com", userToUpdate.getEmail()); // Email не изменился
        assertEquals(List.of("UPDATED"), userToUpdate.getRoles()); // Роли обновились
    }

    @Test
    @DisplayName("Выбрасывает JsonOperationException при невалидном JSON")
    void shouldThrowExceptionForInvalidJson() {
        String invalidJson = "{\"id\":1, \"name\":\"Test User\"";
        assertThrows(JsonUtils.JsonOperationException.class, () -> JsonUtils.fromJson(invalidJson, User.class));
    }
}
