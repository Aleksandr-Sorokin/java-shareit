package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoId;
import ru.practicum.shareit.configuration.PageHandlerRequest;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final static LocalDateTime DATE_TIME_LAST = LocalDateTime.parse("2022-11-10T10:10:10");
    private final static LocalDateTime DATE_TIME_NEXT = LocalDateTime.parse("2022-11-12T10:10:10");

    @BeforeEach
    void addData() {
        ItemDto itemDtoOne = createItemDtoOne();
        ItemDto itemDtoTwo = createItemDtoTwo();
        userService.addUser(createUserDto());
        userService.addUser(createUserDtoTwo());
        userService.addUser(createUserDtoThree());
        itemService.addItem(1L, itemDtoOne);
        itemService.addItem(1L, itemDtoTwo);
        BookingDtoId bookingDtoIdFirstUsertwo = createBookingDtoIdLast();
        BookingDtoId bookingDtoIdSecondUsertwo = createBookingDtoIdNext();
        BookingDtoId bookingDtoIdThreeUserThree = createBookingDtoIdLast();
        bookingDtoIdThreeUserThree.setId(3L);
        bookingDtoIdThreeUserThree.setBookerId(3L);
        bookingDtoIdThreeUserThree.setItemId(2L);
        bookingDtoIdThreeUserThree.setStart(DATE_TIME_LAST.plusDays(1));
        bookingDtoIdThreeUserThree.setEnd(DATE_TIME_NEXT.plusDays(1));
        bookingService.addBooking(2L, bookingDtoIdFirstUsertwo);
        bookingService.addBooking(2L, bookingDtoIdSecondUsertwo);
        bookingService.addBooking(3L, bookingDtoIdThreeUserThree);
    }

    UserDto createUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("NameDto");
        dto.setEmail("email@email.ru");
        return dto;
    }

    UserDto createUserDtoTwo() {
        UserDto user = new UserDto();
        user.setId(2L);
        user.setName("NameDtoTwo");
        user.setEmail("emailTwo@email.ru");
        return user;
    }

    UserDto createUserDtoThree() {
        UserDto dto = new UserDto();
        dto.setId(3L);
        dto.setName("NameDtoThree");
        dto.setEmail("emailThree@email.ru");
        return dto;
    }

    BookingDtoId createBookingDtoIdLast() {
        BookingDtoId booking = new BookingDtoId();
        booking.setId(1L);
        booking.setStart(DATE_TIME_LAST.minusDays(1));
        booking.setEnd(DATE_TIME_NEXT.minusDays(1));
        booking.setItemId(1L);
        booking.setBookerId(2L);
        booking.setStatus(Status.WAITING);
        return booking;
    }

    BookingDtoId createBookingDtoIdNext() {
        BookingDtoId booking = new BookingDtoId();
        booking.setId(2L);
        booking.setStart(DATE_TIME_LAST);
        booking.setEnd(DATE_TIME_NEXT);
        booking.setItemId(1L);
        booking.setBookerId(2L);
        booking.setStatus(Status.WAITING);
        return booking;
    }

    ItemDto createItemDtoOne() {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("NameItemOne");
        item.setDescription("DescriptionItemOne");
        item.setAvailable(true);
        item.setOwner(createUserDto());
        return item;
    }

    ItemDto createItemDtoTwo() {
        ItemDto item = new ItemDto();
        item.setId(2L);
        item.setName("NameItemTwo");
        item.setDescription("DescriptionItemTwo");
        item.setAvailable(true);
        item.setOwner(createUserDto());
        return item;
    }

    BookingDto createBookingDtoOne() {
        BookingDto booking = new BookingDto();
        booking.setId(1L);
        booking.setStart(DATE_TIME_LAST.minusDays(1));
        booking.setEnd(DATE_TIME_NEXT.minusDays(1));
        booking.setItem(createItemDtoOne());
        booking.setBooker(createUserDtoTwo());
        booking.setStatus(Status.WAITING);
        return booking;
    }

    BookingDto createBookingDtoTwo() {
        BookingDto booking = new BookingDto();
        booking.setId(2L);
        booking.setStart(DATE_TIME_LAST);
        booking.setEnd(DATE_TIME_NEXT);
        booking.setItem(createItemDtoOne());
        booking.setBooker(createUserDtoTwo());
        booking.setStatus(Status.WAITING);
        return booking;
    }

    BookingDto createBookingDtoThree() {
        BookingDto booking = new BookingDto();
        booking.setId(3L);
        booking.setStart(DATE_TIME_LAST);
        booking.setEnd(DATE_TIME_NEXT);
        booking.setItem(createItemDtoTwo());
        booking.setBooker(createUserDtoThree());
        booking.setStatus(Status.REJECTED);
        return booking;
    }

    @Test
    void addBooking() {
        BookingDto expected = createBookingDtoOne();
        expected.setBooker(createUserDtoThree());
        BookingDtoId bookingDtoId = createBookingDtoIdLast();
        BookingDto actual = bookingService.addBooking(3L, bookingDtoId);
        assertEquals(expected, actual);
        assertThrows(NotFoundException.class, () -> bookingService.addBooking(1L, bookingDtoId));
    }

    @Test
    void approvedBooking() {
        BookingDto expected = createBookingDtoOne();
        expected.setStatus(Status.APPROVED);
        BookingDto actual = bookingService.approvedBooking(1L, true, 1L);
        assertEquals(expected, actual);
        assertThrows(ValidationException.class,
                () -> bookingService.approvedBooking(2L, true, 1L));
    }

    @Test
    void getBookingById() {
        BookingDto expected = createBookingDtoOne();
        BookingDto actual = bookingService.getBookingById(1L, 1L);
        assertEquals(expected, actual);
        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(3L, 1L));
    }

    @Test
    void getAllBookingByBookerId() {
        Pageable pageable = PageHandlerRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "start"));
        List<BookingDto> expected = List.of(createBookingDtoTwo(), createBookingDtoOne());
        BookingDto booking = bookingService.approvedBooking(1L, false, 3L);
        assertEquals(expected, bookingService.getAllBookingByBookerId(2L, State.ALL, pageable));
        assertEquals(new ArrayList<>(), bookingService.getAllBookingByBookerId(2L, State.PAST, pageable));
        assertEquals(new ArrayList<>(), bookingService.getAllBookingByBookerId(2L, State.CURRENT, pageable));
        assertEquals(expected, bookingService.getAllBookingByBookerId(2L, State.FUTURE, pageable));
        assertEquals(expected, bookingService.getAllBookingByBookerId(2L, State.WAITING, pageable));
        assertEquals(List.of(booking), bookingService.getAllBookingByBookerId(3L, State.REJECTED, pageable));
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingByBookerId(
                        2L, State.valueOf(State.class, "OTHER"), pageable));
    }

    @Test
    void getAllBookingByOwnerId() {
        Pageable pageable = PageHandlerRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "start"));
        BookingDto bookingDtoThree = createBookingDtoThree();
        bookingDtoThree.setStart(DATE_TIME_LAST.plusDays(1));
        bookingDtoThree.setEnd(DATE_TIME_NEXT.plusDays(1));
        List<BookingDto> expectedThree = List.of(bookingDtoThree, createBookingDtoTwo(), createBookingDtoOne());
        List<BookingDto> expectedTwo = List.of(createBookingDtoTwo(), createBookingDtoOne());
        BookingDto booking = bookingService.approvedBooking(1L, false, 3L);
        assertEquals(expectedThree, bookingService.getAllBookingByOwnerId(1L, State.ALL, pageable));
        assertEquals(new ArrayList<>(), bookingService.getAllBookingByOwnerId(1L, State.PAST, pageable));
        assertEquals(new ArrayList<>(), bookingService.getAllBookingByOwnerId(1L, State.CURRENT, pageable));
        assertEquals(expectedThree, bookingService.getAllBookingByOwnerId(1L, State.FUTURE, pageable));
        assertEquals(expectedTwo, bookingService.getAllBookingByOwnerId(1L, State.WAITING, pageable));
        assertEquals(List.of(booking), bookingService.getAllBookingByOwnerId(1L, State.REJECTED, pageable));
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.getAllBookingByOwnerId(
                        1L, State.valueOf(State.class, "OTHER"), pageable));
    }
}