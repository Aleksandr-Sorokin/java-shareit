package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;
import java.util.List;

public interface UserRepository {
    User addUser(User user);
    User changeUser(long userId, User user);
    User findUserById(long userId);
    List<User> findAll();
    void deleteUser(long userId);
}
