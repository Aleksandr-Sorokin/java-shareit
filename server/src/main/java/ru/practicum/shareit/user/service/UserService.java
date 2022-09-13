package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    UserDto changeUser(long userId, UserDto userDto);

    UserDto findUserById(long userId);

    List<UserDto> findAll();

    void deleteUser(long userId);
}
