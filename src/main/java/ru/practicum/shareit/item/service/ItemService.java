package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoBooking;

import java.util.List;

public interface ItemService {
    ItemDto addItem(long userId, ItemDto itemDto);

    ItemDto changeItem(long userId, long itemId, ItemDto itemDto);

    ItemDtoBooking findItemById(long itemId, long userId);

    List<ItemDtoBooking> findByUserId(long userId);

    List<ItemDto> searchItem(String text);

    void deleteByUserIdAndItemId(long userId, long itemId);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
