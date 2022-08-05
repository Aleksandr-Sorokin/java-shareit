package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemRepository {
    Item addItem(long userId, Item item);
    Item changeItem(long userId, long itemId, Item item);
    Item findItemById(long itemId);
    List<Item> searchItem(String text);
    List<Item> findByUserId(long userId);
    void deleteByUserIdAndItemId(long userId, long itemId);
}
