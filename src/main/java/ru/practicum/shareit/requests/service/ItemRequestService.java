package ru.practicum.shareit.requests.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.requests.model.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.dto.RequestAllWithItemDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addItemRequest(Long userId, ItemRequestDto requestDto);

    List<RequestAllWithItemDto> getAllItemRequestWithResponse(Long userId);

    List<RequestAllWithItemDto> getAllItemRequest(Long userId, Pageable pageable);

    RequestAllWithItemDto getRequestByRequestId(Long userId, Long requestId);
}
