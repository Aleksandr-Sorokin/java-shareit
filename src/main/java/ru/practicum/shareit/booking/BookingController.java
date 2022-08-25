package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoId;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.configuration.PageHandlerRequest;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private ItemService itemService;
    private UserService userService;
    private BookingService bookingService;

    public BookingController(ItemService itemService, UserService userService, BookingService bookingService) {
        this.itemService = itemService;
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto addBooking(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                 @RequestBody BookingDtoId bookingDtoId) {
        checkValidId(bookerId);
        return bookingService.addBooking(bookerId, bookingDtoId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approvedBooking(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                      @PathVariable long bookingId,
                                      @RequestParam boolean approved) {
        if ((Boolean) approved == null) throw new ValidationException("параметр бронировая пуст");
        checkValidId(ownerId);
        checkValidId(bookingId);
        return bookingService.approvedBooking(ownerId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @PathVariable long bookingId) {
        checkValidId(userId);
        checkValidId(bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookingByIdBooker(@RequestHeader("X-Sharer-User-Id") long bookerId,
                                                 @RequestParam(defaultValue = "ALL") State state,
                                                 @RequestParam(defaultValue = "0", required = false) int from,
                                                 @RequestParam(defaultValue = "20", required = false) int size) {
        checkValidId(bookerId);
        if (from < 0 || size < 1) throw new ValidationException("Не корректные данные");
        Pageable pageable = PageHandlerRequest.of(from, size, Sort.by(Sort.Direction.DESC, "start"));
        return bookingService.getAllBookingByBookerId(bookerId, state, pageable);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingByIdOwner(@RequestHeader("X-Sharer-User-Id") long ownerId,
                                                @RequestParam(defaultValue = "ALL") State state,
                                                @RequestParam(defaultValue = "0", required = false) int from,
                                                @RequestParam(defaultValue = "20", required = false) int size) {
        checkValidId(ownerId);
        if (from < 0 || size < 1) throw new ValidationException("Не корректные данные");
        Pageable pageable = PageHandlerRequest.of(from, size, Sort.by(Sort.Direction.DESC, "start"));
        return bookingService.getAllBookingByOwnerId(ownerId, state, pageable);
    }

    private void checkValidId(long id) {
        if (id <= 0) throw new ValidationException("Id меньше или равен 0");
    }

}
