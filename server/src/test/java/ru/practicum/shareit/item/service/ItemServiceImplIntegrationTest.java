package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.dto.BookingDtoId;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.configuration.PageHandlerRequest;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoBooking;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceImplIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final LocalDateTime dateTimeLast = LocalDateTime.parse("2022-11-10T10:10:10");
    private final LocalDateTime dateTimeNext = LocalDateTime.parse("2022-11-12T10:10:10");

    @BeforeEach
    void addData() {
        ItemDto itemDtoOne = createItemDtoOne();
        ItemDto itemDtoTwo = createItemDtoTwo();
        userService.addUser(createUserDto());
        itemService.addItem(1L, itemDtoOne);
        itemService.addItem(1L, itemDtoTwo);
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

    User createUserTwo() {
        User user = new User();
        user.setId(2L);
        user.setName("NameDtoTwo");
        user.setEmail("emailTwo@email.ru");
        return user;
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

    BookingDtoId createBookingDtoIdLast() {
        BookingDtoId booking = new BookingDtoId();
        booking.setId(1L);
        booking.setStart(dateTimeLast.minusDays(1));
        booking.setEnd(dateTimeNext.minusDays(1));
        booking.setItemId(1L);
        booking.setBookerId(2L);
        booking.setStatus(Status.WAITING);
        return booking;
    }

    ItemDtoBooking createItemDtoBooking() {
        ItemDtoBooking dtoBooking = new ItemDtoBooking();
        dtoBooking.setId(1L);
        dtoBooking.setName("NameItemOne");
        dtoBooking.setDescription("DescriptionItemOne");
        dtoBooking.setAvailable(true);
        dtoBooking.setOwner(createUserDto());
        dtoBooking.setComments(new ArrayList<>());
        return dtoBooking;
    }

    ItemDtoBooking createItemDtoBookingTwo() {
        ItemDtoBooking dtoBooking = new ItemDtoBooking();
        dtoBooking.setId(2L);
        dtoBooking.setName("NameItemTwo");
        dtoBooking.setDescription("DescriptionItemTwo");
        dtoBooking.setAvailable(true);
        dtoBooking.setOwner(createUserDto());
        dtoBooking.setComments(new ArrayList<>());
        return dtoBooking;
    }

    CommentDto createCommentDto() {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("CommentDtoText");
        comment.setItem(createItemDtoOne());
        comment.setAuthorName(createUserTwo().getName());
        comment.setCreated(dateTimeNext.plusDays(1));
        return comment;
    }

    @Test
    void searchItem() {
        Pageable pageable = PageHandlerRequest.of(0, 20);
        ItemDto itemDtoOne = createItemDtoOne();
        ItemDto itemDtoTwo = createItemDtoTwo();
        List<ItemDto> expectedList = List.of(itemDtoOne, itemDtoTwo);
        List<ItemDto> expectedListOne = List.of(itemDtoOne);
        List<ItemDto> actualList = itemService.searchItem("NameItem", pageable);
        List<ItemDto> actualListOne = itemService.searchItem("One", pageable);
        assertEquals(expectedList, actualList);
        assertEquals(expectedListOne, actualListOne);
    }

    @Test
    void findItemById() {
        ItemDtoBooking expectedItemDtoBooking = createItemDtoBooking();
        assertTrue(itemService.findItemById(1L, 1L) instanceof ItemDtoBooking);
        assertEquals(expectedItemDtoBooking, itemService.findItemById(1L, 1L));
        assertThrows(NotFoundException.class, () -> itemService.findItemById(99L, 1L));
        assertThrows(NotFoundException.class, () -> itemService.findItemById(1L, 99L));
    }

    @Test
    void findByUserId() {
        List<ItemDtoBooking> expectedItemDtoBooking = List.of(createItemDtoBooking(), createItemDtoBookingTwo());
        Pageable pageable = PageHandlerRequest.of(0, 20);
        assertEquals(expectedItemDtoBooking, itemService.findByUserId(1L, pageable));
        assertThrows(NotFoundException.class, () -> itemService.findByUserId(99L, pageable));
    }

    @Test
    void addComment() {
        userService.addUser(createUserDtoTwo());
        bookingService.addBooking(2L, createBookingDtoIdLast());
        CommentDto expectedCommentDto = createCommentDto();
        CommentDto commentDto = createCommentDto();
        commentDto.setId(null);
        CommentDto actualCommentDto = itemService.addComment(2L, 1L, commentDto);
        assertThrows(ValidationException.class, () -> itemService.addComment(1L, 1L, commentDto));
        assertThrows(NotFoundException.class, () -> itemService.addComment(99L, 1L, commentDto));
        assertThrows(NotFoundException.class, () -> itemService.addComment(2L, 99L, commentDto));
        assertEquals(expectedCommentDto, actualCommentDto);
    }
}
