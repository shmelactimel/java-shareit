package ru.practicum.shareit.item.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.model.Item;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemMapperJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSerializeItem() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setName("ItemName");
        item.setDescription("ItemDescription");
        // Добавьте другие поля item, если необходимо

        String json = objectMapper.writeValueAsString(item);
        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"name\":\"ItemName\"");
        assertThat(json).contains("\"description\":\"ItemDescription\"");
    }

    @Test
    public void testDeserializeItem() throws Exception {
        String json = "{\"id\":1,\"name\":\"ItemName\",\"description\":\"ItemDescription\"}";
        Item item = objectMapper.readValue(json, Item.class);

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("ItemName");
        assertThat(item.getDescription()).isEqualTo("ItemDescription");
    }
}
