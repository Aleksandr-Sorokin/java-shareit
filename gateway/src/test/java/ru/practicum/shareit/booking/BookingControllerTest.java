package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.dto.BookingDtoId;
import ru.practicum.shareit.booking.service.BookingClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingClient bookingClient;
    private final LocalDateTime dateTimeLast = LocalDateTime.parse("2022-11-10T10:10:10");
    private final LocalDateTime dateTimeNext = LocalDateTime.parse("2022-11-12T10:10:10");

    BookingDtoId createBookingDtoIdNext() {
        BookingDtoId booking = new BookingDtoId();
        booking.setId(null);
        booking.setStart(dateTimeLast);
        booking.setEnd(dateTimeNext);
        booking.setItemId(1L);
        booking.setBookerId(2L);
        booking.setStatus(Status.WAITING);
        return booking;
    }

    @Test
    void addBookingBadId() throws Exception {
        String textError = "Id меньше или равен 0";
        BookingDtoId bookingDtoId = createBookingDtoIdNext();
        Mockito.when(bookingClient.addBooking(Mockito.anyLong(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(bookingDtoId, HttpStatus.OK));
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
}