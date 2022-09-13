package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.dto.BookingDtoId;
import ru.practicum.shareit.booking.service.BookingClient;
import ru.practicum.shareit.exeptions.ValidationException;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingControllers {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                             @RequestBody BookingDtoId bookingDtoId) {
        checkValidId(bookerId);
        return bookingClient.addBooking(bookerId, bookingDtoId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approvedBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                  @PathVariable long bookingId,
                                                  @RequestParam boolean approved) {
        checkValidId(ownerId);
        checkValidId(bookingId);
        return bookingClient.approvedBooking(ownerId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long bookingId) {
        checkValidId(userId);
        checkValidId(bookingId);
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingByIdBooker(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                       @RequestParam(defaultValue = "ALL") State state,
                                                       @RequestParam(defaultValue = "0", required = false) int from,
                                                       @RequestParam(defaultValue = "20", required = false) int size) {
        checkValidId(bookerId);
        if (from < 0 || size < 1) throw new ValidationException("Не корректные данные");
        //Pageable pageable = PageHandlerRequest.of(from, size, Sort.by(Sort.Direction.DESC, "start"));
        return bookingClient.getAllBookingByBookerId(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingByIdOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                      @RequestParam(defaultValue = "ALL") State state,
                                                      @RequestParam(defaultValue = "0", required = false) int from,
                                                      @RequestParam(defaultValue = "20", required = false) int size) {
        checkValidId(ownerId);
        if (from < 0 || size < 1) throw new ValidationException("Не корректные данные");
        //Pageable pageable = PageHandlerRequest.of(from, size, Sort.by(Sort.Direction.DESC, "start"));
        return bookingClient.getAllBookingByOwnerId(ownerId, state, from, size);
    }

    private void checkValidId(long id) {
        if (id <= 0) throw new ValidationException("Id меньше или равен 0");
    }

}
