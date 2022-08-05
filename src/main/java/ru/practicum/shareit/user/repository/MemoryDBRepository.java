package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class MemoryDBRepository implements UserRepository {
    long id = 0;
    private Map<Long, User> userDBTemp = new HashMap<>();

    @Override
    public User addUser(User user) {
        user.setId(generateId());
        userDBTemp.put(user.getId(), user);
        return user;
    }

    @Override
    public User changeUser(long userId, User user) {
        if (userDBTemp.containsKey(userId)){
            userDBTemp.put(userId, user);
        } else {
            throw new NotFoundException("Нельзя изменить пользователя, он отсутствует");
        }
        return user;
    }

    @Override
    public User findUserById(long userId) {
        if (userDBTemp.containsKey(userId)){
            return userDBTemp.get(userId);
        } else {
            throw new NotFoundException( userId + "Пользователь отсутствует");
        }
    }

    @Override
    public List<User> findAll() {
        return userDBTemp.values().stream().collect(Collectors.toList());
    }

    @Override
    public void deleteUser(long userId) {
        userDBTemp.remove(userId);
    }

    private long generateId() {
        return id += 1;
    }
}
