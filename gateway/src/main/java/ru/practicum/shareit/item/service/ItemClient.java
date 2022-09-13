package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.util.ArrayList;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> findItemById(long itemId, long userId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        String path = "/{itemId}";
        return get(path, userId, parameters);
    }

    public ResponseEntity<Object> searchItem(String text, Integer from, Integer size) {
        if (text.isBlank()) return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        String path = "/search?text={text}&from={from}&size={size}";
        return get(path, null, parameters);
    }

    public ResponseEntity<Object> findByUserId(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        String path = "?from={from}&size={size}";
        return get(path, userId, parameters);
    }

    public ResponseEntity<Object> addItem(long userId, ItemDto itemDto) {
        String path = "";
        return post(path, userId, itemDto);
    }

    public ResponseEntity<Object> changeItem(long userId, long itemId, ItemDto itemDto) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        String path = "/{itemId}";
        return patch(path, userId, parameters, itemDto);
    }

    public ResponseEntity<Object> deleteByUserIdAndItemId(long userId, long itemId) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        String path = "/{itemId}";
        return delete(path, userId, parameters);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CommentDto commentDto) {
        Map<String, Object> parameters = Map.of(
                "itemId", itemId
        );
        String path = "/{itemId}/comment";
        return post(path, userId, parameters, commentDto);
    }
}
