package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemClient;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @PathVariable long itemId) {
        checkValidId(userId);
        checkValidId(itemId);
        return itemClient.findItemById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                             @RequestParam(defaultValue = "0", required = false) int from,
                                             @RequestParam(defaultValue = "20", required = false) int size) {
        if (from < 0 || size < 1) throw new ValidationException("Не корректные данные");
        return itemClient.searchItem(text, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> findByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                               @RequestParam(defaultValue = "0", required = false) int from,
                                               @RequestParam(defaultValue = "20", required = false) int size) {
        checkValidId(userId);
        if (from < 0 || size < 1) throw new ValidationException("Не корректные данные");
        return itemClient.findByUserId(userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        checkValidId(userId);
        if (itemDto == null) throw new ValidationException("Отсутствуют данные по вещи");
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> changeItem(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                                             @PathVariable(value = "itemId") long itemId,
                                             @RequestBody ItemDto itemDto) {
        checkValidId(userId);
        checkValidId(itemId);
        if (itemDto == null) throw new ValidationException("Отсутствуют данные по вещи");
        return itemClient.changeItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteByUserIdAndItemId(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable long itemId) {
        checkValidId(userId);
        checkValidId(itemId);
        itemClient.deleteByUserIdAndItemId(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId, @RequestBody CommentDto commentDto) {
        checkValidId(userId);
        checkValidId(itemId);
        if (commentDto.getText().isBlank()) {
            throw new ValidationException("Коментарий отсутствует");
        }
        return itemClient.addComment(userId, itemId, commentDto);
    }

    private void checkValidId(long id) {
        if (id <= 0) throw new ValidationException("Id не должен быть отрицательным");
    }
}
