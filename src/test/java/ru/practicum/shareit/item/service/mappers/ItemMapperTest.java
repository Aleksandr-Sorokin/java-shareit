package ru.practicum.shareit.item.service.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mappers.BookingMapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDtoId;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.dto.ItemForRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.mappers.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private BookingMapping bookingMapping;
    private ItemMapper itemMapper;

    @BeforeEach
    void setItemMapper() {
        itemMapper = new ItemMapper(new ModelMapper(), userMapper, bookingMapping);
    }

    private final static LocalDateTime DATE_TIME = LocalDateTime.parse("2022-11-11T10:10:10");
    private final static LocalDateTime DATE_TIME_LAST = LocalDateTime.parse("2022-11-10T10:10:10");
    private final static LocalDateTime DATE_TIME_NEXT = LocalDateTime.parse("2022-11-12T10:10:10");

    UserDto createUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("NameDto");
        dto.setEmail("email@email.ru");
        return dto;
    }

    User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("NameDto");
        user.setEmail("email@email.ru");
        return user;
    }

    User createUserTwo() {
        User user = new User();
        user.setId(2L);
        user.setName("NameDtoTwo");
        user.setEmail("emailTwo@email.ru");
        return user;
    }

    ItemRequest createRequest() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("DescriptionRequest");
        request.setRequestor(createUserTwo());
        request.setCreated(DATE_TIME);
        return request;
    }

    Item createItemNullCommentsNullRequest() {
        Item item = new Item();
        item.setId(1L);
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(createUser());
        return item;
    }

    ItemDto createItemDtoNullCommentsNullRequest() {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(createUserDto());
        return item;
    }

    CommentDto createCommentDto() {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setAuthorName("NameDtoTwo");
        comment.setText("comment");
        comment.setItem(createItemDtoNullCommentsNullRequest());
        comment.setCreated(DATE_TIME);
        return comment;
    }

    Comment createComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(createUserTwo());
        comment.setText("comment");
        comment.setItem(createItemNullCommentsNullRequest());
        comment.setCreated(DATE_TIME);
        return comment;
    }

    Booking createBookingLast() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(DATE_TIME_LAST.minusDays(1));
        booking.setEnd(DATE_TIME_NEXT.minusDays(1));
        booking.setItem(createItemNullCommentsNullRequest());
        booking.setBooker(createUserTwo());
        booking.setStatus(Status.WAITING);
        return booking;
    }

    Booking createBookingNext() {
        Booking booking = new Booking();
        booking.setId(2L);
        booking.setStart(DATE_TIME_LAST);
        booking.setEnd(DATE_TIME_NEXT);
        booking.setItem(createItemNullCommentsNullRequest());
        booking.setBooker(createUserTwo());
        booking.setStatus(Status.WAITING);
        return booking;
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

    ItemDtoBooking createItemDtoBookingOther() {
        ItemDtoBooking dtoBooking = new ItemDtoBooking();
        dtoBooking.setId(1L);
        dtoBooking.setName("NameItem");
        dtoBooking.setDescription("DescriptionItem");
        dtoBooking.setAvailable(true);
        dtoBooking.setOwner(createUserDto());
        dtoBooking.setComments(List.of(createCommentDto()));
        return dtoBooking;
    }

    ItemDtoBooking createItemDtoBookingOwner() {
        ItemDtoBooking dtoBooking = new ItemDtoBooking();
        dtoBooking.setId(1L);
        dtoBooking.setName("NameItem");
        dtoBooking.setDescription("DescriptionItem");
        dtoBooking.setAvailable(true);
        dtoBooking.setOwner(createUserDto());
        dtoBooking.setLastBooking(createBookingDtoIdLast());
        dtoBooking.setNextBooking(createBookingDtoIdNext());
        dtoBooking.setComments(List.of(createCommentDto()));
        return dtoBooking;
    }

    Item createItemWithRequest() {
        Item item = new Item();
        item.setId(1L);
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(createUser());
        item.setRequest(createRequest());
        return item;
    }

    ItemForRequestDto createItemForRequestDto() {
        ItemForRequestDto item = new ItemForRequestDto();
        item.setId(1L);
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwnerId(1L);
        item.setRequestId(1L);
        return item;
    }

    @Test
    void toItemEntity() {
        User user = createUser();
        ItemDto itemDto = createItemDtoNullCommentsNullRequest();
        Item expectedItem = createItemNullCommentsNullRequest();
        ItemRequest request = null;
        Item actualItem = itemMapper.toItemEntity(user, itemDto, request);
        assertEquals(expectedItem, actualItem);
        assertThrows(ValidationException.class, () -> itemMapper.toItemEntity(user, null, request));
    }

    @Test
    void toItemDto() {
        User user = createUser();
        Mockito.when(userMapper.toUserDto(user)).thenReturn(createUserDto());
        Item item = createItemNullCommentsNullRequest();
        ItemDto expectedItemDto = createItemDtoNullCommentsNullRequest();
        ItemDto actualItemDto = itemMapper.toItemDto(item);
        assertEquals(expectedItemDto, actualItemDto);
        assertThrows(ValidationException.class, () -> itemMapper.toItemDto(null));
    }

    @Test
    void toCommentDto() {
        Mockito.when(userMapper.toUserDto(createUser())).thenReturn(createUserDto());
        Comment comment = createComment();
        CommentDto expectedCommentDto = createCommentDto();
        CommentDto actualCommentDto = itemMapper.toCommentDto(comment);
        assertEquals(expectedCommentDto, actualCommentDto);
    }

    @Test
    void toComment() {
        CommentDto commentDto = createCommentDto();
        User author = createUserTwo();
        Item item = createItemNullCommentsNullRequest();
        Comment expectedComment = createComment();
        Comment actualComment = itemMapper.toComment(author, item, commentDto);
        assertEquals(expectedComment, actualComment);
    }

    @Test
    void toItemDtoBooking() {
        Item item = createItemNullCommentsNullRequest();
        Comment comment = createComment();
        List<Comment> comments = List.of(comment);
        User owner = createUser();
        User otherUser = createUserTwo();
        Booking last = createBookingLast();
        Booking next = createBookingNext();
        Mockito.when(userMapper.toUserDto(owner)).thenReturn(createUserDto());
        ItemDtoBooking actualWithOwner = createItemDtoBookingOwner();
        ItemDtoBooking actualWithOther = createItemDtoBookingOther();
        ItemDtoBooking expectedItemDtoBookingOwner = itemMapper.toItemDtoBooking(item, owner, last, next, comments);
        ItemDtoBooking expectedItemDtoBookingOther = itemMapper.toItemDtoBooking(item, otherUser, last, next, comments);
        assertEquals(expectedItemDtoBookingOther, actualWithOther);
        assertEquals(expectedItemDtoBookingOwner, actualWithOwner);
        assertThrows(ValidationException.class, () -> itemMapper
                .toItemDtoBooking(null, otherUser, last, next, comments));
    }

    @Test
    void toDtoItemForRequest() {
        Item item = createItemNullCommentsNullRequest();
        Item itemWithRequest = createItemWithRequest();
        ItemForRequestDto actualWithRequest = createItemForRequestDto();
        ItemForRequestDto actualItemNullRequest = createItemForRequestDto();
        actualItemNullRequest.setRequestId(null);
        ItemForRequestDto expectedItemWithRequest = itemMapper.toDtoItemForRequest(itemWithRequest);
        ItemForRequestDto expectedItemNullRequest = itemMapper.toDtoItemForRequest(item);
        assertThrows(ValidationException.class, () -> itemMapper.toDtoItemForRequest(null));
        assertEquals(expectedItemNullRequest, actualItemNullRequest);
        assertEquals(expectedItemWithRequest, actualWithRequest);
    }
}