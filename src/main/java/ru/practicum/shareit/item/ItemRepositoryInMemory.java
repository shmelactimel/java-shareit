package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.error.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryInMemory implements ItemRepository {
    private final Map<Long, Item> storage = new HashMap<>();
    private final Map<Long, Set<Long>> userItemsMap = new HashMap<>();
    private Long counter = 1L;

    @Override
    public Item addItem(Item item) {
        if (item.getId() == null) {
            item.setId(counter);
            counter++;
        }
        storage.put(item.getId(), item);
        userItemsMap.computeIfAbsent(item.getOwner(), k -> new HashSet<>()).add(item.getId());
        return item;
    }

    @Override
    public void updateItem(Item item) {
        storage.put(item.getId(), item);
    }

    @Override
    public Item getItem(Long id) {
        Item item = storage.get(id);
        if (item == null) {
            throw new EntityNotFoundException("Item with id " + id + " not found");
        }
        return item;
    }

    @Override
    public Set<Item> getUserItems(Long userId) {
        Set<Long> itemIds = userItemsMap.getOrDefault(userId, Collections.emptySet());
        Set<Item> userItems = itemIds.stream()
                .map(storage::get)
                .collect(Collectors.toSet());

        return userItems;
    }

    @Override
    public Set<Item> getItems() {
        return storage.values().stream().collect(Collectors.toSet());
    }

    @Override
    public void deleteItem(Item item) {
        storage.remove(item.getId());
    }

    @Override
    public void deleteUserItems(Long userId) {
        List<Item> items = List.copyOf(storage.values().stream()
                .filter(x -> x.getOwner().equals(userId))
                .collect(Collectors.toList()));
        items.stream().forEach(x -> storage.remove(x.getId()));
    }
}