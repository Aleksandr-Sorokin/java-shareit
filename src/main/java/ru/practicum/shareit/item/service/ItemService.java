package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {
    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto changeItem(long userId, long itemId, ItemDto itemDto);

    ItemDto findItemById(long itemId);

    List<ItemDto> findByUserId(long userId);

    List<ItemDto> searchItem(String text);

    void deleteByUserIdAndItemId(long userId, long itemId);
}
