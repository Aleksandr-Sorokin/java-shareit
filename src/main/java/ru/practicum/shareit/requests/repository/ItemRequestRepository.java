package ru.practicum.shareit.requests.repository;

import ru.practicum.shareit.requests.model.ItemRequest;

public interface ItemRequestRepository {
    ItemRequest addRequest(long user, ItemRequest itemRequest);
}
