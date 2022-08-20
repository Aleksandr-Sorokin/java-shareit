package ru.practicum.shareit.requests.model.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    private final long id;
    private final String description;
    private final UserDto requestor;
    private final LocalDateTime created;
}
