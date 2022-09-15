package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoId;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.configuration.PageHandlerRequest;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                 @RequestBody BookingDtoId bookingDtoId) {
        return bookingService.addBooking(bookerId, bookingDtoId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                      @PathVariable long bookingId,
                                      @RequestParam boolean approved) {
        return bookingService.approvedBooking(ownerId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingByIdBooker(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                 @RequestParam(defaultValue = "ALL") State state,
                                                 @RequestParam(defaultValue = "0", required = false) int from,
                                                 @RequestParam(defaultValue = "20", required = false) int size) {
        Pageable pageable = PageHandlerRequest.of(from, size, Sort.by(Sort.Direction.DESC, "start"));
        return bookingService.getAllBookingByBookerId(bookerId, state, pageable);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByIdOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                @RequestParam(defaultValue = "ALL") State state,
                                                @RequestParam(defaultValue = "0", required = false) int from,
                                                @RequestParam(defaultValue = "20", required = false) int size) {
        Pageable pageable = PageHandlerRequest.of(from, size, Sort.by(Sort.Direction.ASC, "start"));
        return bookingService.getAllBookingByOwnerId(ownerId, state, pageable);
    }
}
