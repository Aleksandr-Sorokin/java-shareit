package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoId;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(long bookerId, BookingDtoId bookingDtoId);

    BookingDto approvedBooking(long ownerId, boolean approved, long bookingId);

    BookingDto getBookingById(long userId, long bookingId);

    List<BookingDto> getAllBookingByBookerId(long bookerId, State state, Pageable pageable);

    List<BookingDto> getAllBookingByOwnerId(long ownerId, State state, Pageable pageable);
}
