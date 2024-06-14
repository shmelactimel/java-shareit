package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Sql(scripts = "data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemMapperTest {

    private final ItemMapper itemMapper;

    @Test
    public void toModelNullTest() {
        assertThat(itemMapper.toModel(null, null, null)).isNull();
    }

    @Test
    public void toDtoNullTest() {
        assertThat(itemMapper.toDto((Item) null)).isNull();
    }

    @Test
    public void toDtoListNullTest() {
        assertThat(itemMapper.toDto((List<Item>) null)).isNull();
    }

}