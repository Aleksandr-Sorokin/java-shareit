package ru.practicum.shareit.requests.dto;

import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;
import java.time.LocalDateTime;

/**
 * // TODO .
 */

@Data
public class ItemRequestDto {
    private final long id;
    private final String description;
    private final UserDto requestor;
    private final LocalDateTime created;
}
