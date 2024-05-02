package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;
import java.util.Collections;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        log.info("add item for user {}", userId);
        ItemDto addedItem = itemService.addItem(userId, itemDto);
        if (addedItem == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId + " does not exist");
        }
        return addedItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable Long itemId) {
        log.info("update item");
        Optional<ItemDto> updatedItemDto = itemService.updateItem(userId, itemDto, itemId);
        if (updatedItemDto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user id %d not owner or booking does not exist");
        }
        return updatedItemDto.get();
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") @PathVariable Long itemId) {
        log.info("get item id {}", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping
    public Set<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("get user items");
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public Set<ItemDto> searchForAnItem(@RequestParam(required = false) String text) {
        if (text == null || text.isBlank()) {
            // Если параметр text не указан или пуст, вернуть пустой набор элементов
            return Collections.emptySet();
        }
        log.info("get user items {}", text);
        return itemService.searchItem(text);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        log.info("delete item id {}", itemId);
        itemService.deleteItem(itemId);
    }
}