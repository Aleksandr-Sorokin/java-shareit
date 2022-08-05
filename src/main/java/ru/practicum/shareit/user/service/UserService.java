package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto addUser(User user);

    UserDto changeUser(long userId, UserDto userDto);

    UserDto findUserById(long userId);

    List<UserDto> findAll();

    void deleteUser(long userId);
}
