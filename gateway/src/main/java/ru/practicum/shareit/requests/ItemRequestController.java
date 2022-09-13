package ru.practicum.shareit.requests;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.requests.model.dto.ItemRequestDto;
import ru.practicum.shareit.requests.service.ItemRequestClient;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private ItemRequestClient requestClient;

    public ItemRequestController(ItemRequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestBody ItemRequestDto requestDto) {
        checkValidId(userId);
        return requestClient.addItemRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemRequestWithResponse(@RequestHeader("X-Sharer-User-Id") Long userId) {
        checkValidId(userId);
        return requestClient.getAllItemRequestWithResponse(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "0", required = false) int from,
                                                @RequestParam(defaultValue = "20", required = false) int size) {
        checkValidId(userId);
        if (from < 0 || size < 1) throw new ValidationException("Не корректные данные");
        //Pageable pageable = PageHandlerRequest.of(from, size, Sort.by(Sort.Direction.DESC, "created"));
        return requestClient.getAllItemRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestByRequestId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                        @PathVariable Long requestId) {
        checkValidId(userId);
        checkValidId(requestId);
        return requestClient.getRequestByRequestId(userId, requestId);
    }

    private void checkValidId(long id) {
        if (id <= 0) throw new ValidationException("Id меньше или равен 0");
    }
}
