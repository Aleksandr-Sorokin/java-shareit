package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.user.model.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    public UserClient(@Value("${shareit-server.url}") String serviceUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> findUserById(long userId) {
        StringBuilder path = new StringBuilder("/" + userId);
        return get(String.valueOf(path));
    }

    public ResponseEntity<Object> addUser(UserDto userDto) {
        String path = "";
        return post(path, userDto);
    }

    public ResponseEntity<Object> changeUser(long userId, UserDto userDto) {
        StringBuilder path = new StringBuilder("/" + userId);
        return patch(String.valueOf(path), userDto);
    }

    public ResponseEntity<Object> deleteUser(long userId) {
        StringBuilder path = new StringBuilder("/" + userId);
        return delete(String.valueOf(path));
    }

    public ResponseEntity<Object> findAll() {
        String path = "";
        return get(path);
    }
}
