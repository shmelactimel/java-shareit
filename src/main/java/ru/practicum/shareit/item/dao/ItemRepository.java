package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select it from Item as it" +
            " where it.available = true" +
            " and (lower(it.name) like lower(concat('%', ?1,'%'))" +
            " or lower(it.description) like lower(concat('%', ?1,'%')))")
    List<Item> search(String text);

    List<Item> findAllByOwnerIdOrderById(Long userId);
}