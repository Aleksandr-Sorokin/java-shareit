package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoBooking;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDtoBooking findItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @PathVariable long itemId) {
        checkValidId(userId);
        checkValidId(itemId);
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        if (text.isBlank()) return new ArrayList<>();
        return itemService.searchItem(text);
    }

    @GetMapping
    public List<ItemDtoBooking> findByUserId(@RequestHeader("X-Sharer-User-Id") long userId) {
        checkValidId(userId);
        return itemService.findByUserId(userId);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        checkValidId(userId);
        if (itemDto == null) throw new ValidationException("Отсутствуют данные по вещи");
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto changeItem(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                              @PathVariable(value = "itemId") long itemId,
                              @RequestBody ItemDto itemDto) {
        checkValidId(userId);
        checkValidId(itemId);
        if (itemDto == null) throw new ValidationException("Отсутствуют данные по вещи");
        return itemService.changeItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteByUserIdAndItemId(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable long itemId) {
        checkValidId(userId);
        checkValidId(itemId);
        itemService.deleteByUserIdAndItemId(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId, @RequestBody CommentDto commentDto) {
        checkValidId(userId);
        checkValidId(itemId);
        if (commentDto.getText().isBlank()) {
            throw new ValidationException("Коментарий отсутствует");
        }
        commentDto.setCreated(LocalDateTime.now());
        return itemService.addComment(userId, itemId, commentDto);
    }

    private void checkValidId(long id) {
        if (id <= 0) throw new ValidationException("Id не должен быть отрицательным");
    }
}
