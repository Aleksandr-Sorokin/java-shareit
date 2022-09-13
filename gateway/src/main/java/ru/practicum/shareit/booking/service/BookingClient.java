package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.dto.BookingDtoId;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exeptions.ValidationException;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addBooking(long bookerId, BookingDtoId bookingDtoId) {
        checkValidTime(bookingDtoId);
        String path = "";
        return post("", bookerId, bookingDtoId);
    }

    public ResponseEntity<Object> approvedBooking(long ownerId, boolean approved, long bookingId) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        String path = "/{bookingId}?approved={approved}";
        return patch("/" + bookingId + "?approved={approved}", ownerId, parameters);
    }

    public ResponseEntity<Object> getBookingById(long userId, long bookingId) {
        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId
        );
        String path = "/{bookingId}";
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllBookingByBookerId(long userId, State state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        String path = "?state={state}&from={from}&size={size}";
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllBookingByOwnerId(long userId, State state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        String path = "/owner?state={state}&from={from}&size={size}";
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }


    /*public ResponseEntity<Object> bookItem(long userId, BookItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }*/

    private void checkValidTime(BookingDtoId bookingDtoId) {
        LocalDateTime startTime = bookingDtoId.getStart();
        LocalDateTime endTime = bookingDtoId.getEnd();
        if (endTime.isBefore(LocalDateTime.now())) throw new ValidationException("Время окончания не корректно");
        if (startTime.isAfter(endTime)) throw new ValidationException("Время окончания раньше начала");
        if (startTime.isBefore(LocalDateTime.now())) throw new ValidationException("Время начала не корректно");
    }
}