package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserClient;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        checkValidId(userId);
        return userClient.findUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto userDto) {
        return userClient.addUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> changeUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        checkValidId(userId);
        return userClient.changeUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        checkValidId(userId);
        userClient.deleteUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUser() {
        return userClient.findAll();
    }

    private void checkValidId(long id) {
        if (id <= 0) throw new ValidationException("Id меньше или равен 0");
    }
}