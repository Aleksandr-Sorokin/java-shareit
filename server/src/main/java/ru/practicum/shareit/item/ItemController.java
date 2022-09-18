package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.configuration.PageHandlerRequest;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoBooking;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemDtoBooking findItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                                       @PathVariable long itemId) {
        return itemService.findItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @RequestParam(defaultValue = "0", required = false) int from,
                                    @RequestParam(defaultValue = "20", required = false) int size) {
        Pageable pageable = PageHandlerRequest.of(from, size);
        return itemService.searchItem(text, pageable);
    }

    @GetMapping
    public List<ItemDtoBooking> findByUserId(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @RequestParam(defaultValue = "0", required = false) int from,
                                             @RequestParam(defaultValue = "20", required = false) int size) {
        Pageable pageable = PageHandlerRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));
        return itemService.findByUserId(userId, pageable);
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto changeItem(@RequestHeader(value = "X-Sharer-User-Id") long userId,
                              @PathVariable(value = "itemId") long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.changeItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteByUserIdAndItemId(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @PathVariable long itemId) {
        itemService.deleteByUserIdAndItemId(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @PathVariable long itemId, @RequestBody CommentDto commentDto) {
        commentDto.setCreated(LocalDateTime.now());
        return itemService.addComment(userId, itemId, commentDto);
    }
}
