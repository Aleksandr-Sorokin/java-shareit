package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoId;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;
    private final static LocalDateTime DATE_TIME_LAST = LocalDateTime.parse("2022-11-10T10:10:10");
    private final static LocalDateTime DATE_TIME_NEXT = LocalDateTime.parse("2022-11-12T10:10:10");

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

    ItemDto createItemDtoNullCommentsNullRequest() {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(createUserDto());
        return item;
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

    BookingDto createBookingDtoOne() {
        BookingDto booking = new BookingDto();
        booking.setId(1L);
        booking.setStart(DATE_TIME_LAST.minusDays(1));
        booking.setEnd(DATE_TIME_NEXT.minusDays(1));
        booking.setItem(createItemDtoNullCommentsNullRequest());
        booking.setBooker(createUserDtoTwo());
        booking.setStatus(Status.WAITING);
        return booking;
    }

    BookingDto createBookingDtoTwo() {
        BookingDto booking = new BookingDto();
        booking.setId(2L);
        booking.setStart(DATE_TIME_LAST);
        booking.setEnd(DATE_TIME_NEXT);
        booking.setItem(createItemDtoNullCommentsNullRequest());
        booking.setBooker(createUserDtoTwo());
        booking.setStatus(Status.WAITING);
        return booking;
    }

    @Test
    void addBooking() throws Exception {
        BookingDtoId bookingDtoId = createBookingDtoIdNext();
        bookingDtoId.setId(null);
        BookingDto expectedBookingDto = createBookingDtoOne();
        Mockito.when(bookingService.addBooking(Mockito.anyLong(), Mockito.any())).thenReturn(expectedBookingDto);
        mockMvc.perform(post("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .content(objectMapper.writeValueAsString(bookingDtoId))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedBookingDto)));
    }

    @Test
    void addBookingBadId() throws Exception {
        String textError = "Id меньше или равен 0";
        BookingDtoId bookingDtoId = createBookingDtoIdNext();
        bookingDtoId.setId(null);
        BookingDto expectedBookingDto = createBookingDtoOne();
        Mockito.when(bookingService.addBooking(Mockito.anyLong(), Mockito.any())).thenReturn(expectedBookingDto);
        mockMvc.perform(post("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0)
                        .content(objectMapper.writeValueAsString(bookingDtoId))
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(textError, result.getResolvedException().getMessage()));
    }

    @Test
    void approvedBooking() throws Exception {
        BookingDto expectedBookingDto = createBookingDtoOne();
        Mockito.when(bookingService.approvedBooking(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong()))
                .thenReturn(expectedBookingDto);
        mockMvc.perform(patch("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", String.valueOf(true))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedBookingDto)));
    }

    @Test
    void approvedBookingNotParamApproved() throws Exception {
        BookingDto expectedBookingDto = createBookingDtoOne();
        Mockito.when(bookingService.approvedBooking(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong()))
                .thenReturn(expectedBookingDto);
        mockMvc.perform(patch("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getBookingById() throws Exception {
        BookingDto expectedBookingDto = createBookingDtoOne();
        Mockito.when(bookingService.getBookingById(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(expectedBookingDto);
        mockMvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedBookingDto)));
    }

    @Test
    void getBookingByIdBooker() throws Exception {
        List<BookingDto> expectedList = List.of(createBookingDtoOne());
        Mockito.when(bookingService.getAllBookingByBookerId(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(expectedList);
        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .param("state", String.valueOf(State.WAITING))
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(2))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedList)));
    }

    @Test
    void getBookingByIdBookerBadParam() throws Exception {
        String textError = "Не корректные данные";
        List<BookingDto> expectedList = List.of(createBookingDtoOne());
        Mockito.when(bookingService.getAllBookingByBookerId(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(expectedList);
        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                        .param("state", String.valueOf(State.WAITING))
                        .param("from", String.valueOf(-1))
                        .param("size", String.valueOf(0))
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(textError, result.getResolvedException().getMessage()));
    }

    @Test
    void getBookingByIdBookerNotParam() throws Exception {
        BookingDto bookingDtoOne = createBookingDtoOne();
        BookingDto bookingDtoTwo = createBookingDtoTwo();
        bookingDtoTwo.setStatus(Status.APPROVED);
        List<BookingDto> expectedList = List.of(bookingDtoOne, bookingDtoTwo);
        Mockito.when(bookingService.getAllBookingByBookerId(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(expectedList);
        mockMvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedList)));
    }

    @Test
    void getBookingByIdOwner() throws Exception {
        List<BookingDto> expectedList = List.of(createBookingDtoOne());
        Mockito.when(bookingService.getAllBookingByOwnerId(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(expectedList);
        mockMvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", String.valueOf(State.WAITING))
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(2))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedList)));
    }

    @Test
    void getBookingByIdOwnerBadParam() throws Exception {
        String textError = "Не корректные данные";
        List<BookingDto> expectedList = List.of(createBookingDtoOne());
        Mockito.when(bookingService.getAllBookingByOwnerId(Mockito.anyLong(), Mockito.any(), Mockito.any()))
                .thenReturn(expectedList);
        mockMvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", String.valueOf(State.WAITING))
                        .param("from", String.valueOf(-1))
                        .param("size", String.valueOf(0))
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(textError, result.getResolvedException().getMessage()));
    }
}