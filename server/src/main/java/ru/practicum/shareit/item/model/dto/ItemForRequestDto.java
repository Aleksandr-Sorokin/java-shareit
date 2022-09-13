package ru.practicum.shareit.item.model.dto;

import lombok.Data;

@Data
public class ItemForRequestDto {
    Long id;
    String name;
    String description;
    boolean available;
    Long ownerId;
    Long requestId;
}
