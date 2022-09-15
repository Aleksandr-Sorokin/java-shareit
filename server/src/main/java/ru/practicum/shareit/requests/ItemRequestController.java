package ru.practicum.shareit.requests;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.configuration.PageHandlerRequest;
import ru.practicum.shareit.requests.model.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.dto.RequestAllWithItemDto;
import ru.practicum.shareit.requests.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private ItemRequestService requestService;

    public ItemRequestController(ItemRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestBody ItemRequestDto requestDto) {
        return requestService.addItemRequest(userId, requestDto);
    }

    @GetMapping
    public List<RequestAllWithItemDto> getAllItemRequestWithResponse(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getAllItemRequestWithResponse(userId);
    }

    @GetMapping("/all")
    public List<RequestAllWithItemDto> getAllRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "0", required = false) int from,
                                                     @RequestParam(defaultValue = "20", required = false) int size) {
        Pageable pageable = PageHandlerRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created"));
        return requestService.getAllItemRequest(userId, pageable);
    }

    @GetMapping("/{requestId}")
    public RequestAllWithItemDto getRequestByRequestId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @PathVariable Long requestId) {
        return requestService.getRequestByRequestId(userId, requestId);
    }
}
