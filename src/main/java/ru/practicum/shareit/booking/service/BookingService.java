package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoId;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(long bookerId, BookingDtoId bookingDtoId);

    BookingDto approvedBooking(long ownerId, boolean approved, long bookingId);

    BookingDto getBookingById(long userId, long bookingId);

    List<BookingDto> getAllBookingByBookerId(long bookerId, State state);

    List<BookingDto> getAllBookingByOwnerId(long ownerId, State state);
}
