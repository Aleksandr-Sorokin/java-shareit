package ru.practicum.shareit.requests.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.requests.model.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";
    private String responsNotValid = "Нет данных для запроса";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serviceUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> addItemRequest(long userId, ItemRequestDto requestDto) {
        if (requestDto == null || requestDto.getDescription() == null
                || requestDto.getDescription().isBlank()) throw new ValidationException(responsNotValid);
        String path = "";
        return post(path, userId, requestDto);
    }

    public ResponseEntity<Object> getAllItemRequestWithResponse(long userId) {
        String path = "";
        return get(path, userId);
    }

    public ResponseEntity<Object> getAllItemRequest(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        StringBuilder path = new StringBuilder("/all?from=" + from + "&size=" + size);
        return get(String.valueOf(path), userId, parameters);
    }

    public ResponseEntity<Object> getRequestByRequestId(long userId, long requestId) {
        Map<String, Object> parameters = Map.of(
                "requestId", requestId
        );
        StringBuilder path = new StringBuilder("/" + requestId);
        return get(String.valueOf(path), userId, parameters);
    }
}
