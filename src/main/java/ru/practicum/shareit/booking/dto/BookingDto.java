package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import java.time.LocalDateTime;

/**
 * // TODO .
 */

@Data
public class BookingDto {
    private final long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Item item;
    private final UserDto booker;
    private final Status status;
}
