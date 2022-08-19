package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.configuration.MapperUtil;
import ru.practicum.shareit.exeptions.DuplicateEmail;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.mappers.UserMapper;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private ModelMapper modelMapper;
    private UserRepository userRepository;
    private UserMapper userMapper;
    private String errorNotFoundText = "Такого пользователя нет";
    private String errorDuplicateEmailText = "Такой Email уже существует";

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = userMapper.toUserEntity(userDto);
        userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto changeUser(long userId, UserDto userDto) {
        User user = getUserFromRepository(userId);
        userDto.setId(userId);
        checkDuplicateEmail(userId, userDto.getEmail());
        modelMapper.map(userDto, user);
        userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserDto findUserById(long userId) {
        return userMapper.toUserDto(getUserFromRepository(userId));
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        return MapperUtil.convertList(users, user -> userMapper.toUserDto(user));
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }

    private void checkDuplicateEmail(long userId, String email) {
        if (userRepository.findAll().stream()
                .filter(n -> n.getId() != userId)
                .filter(o -> o.getEmail().equals(email))
                .findAny().isPresent()) {
            throw new DuplicateEmail(errorDuplicateEmailText);
        }
    }

    private User getUserFromRepository(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(errorNotFoundText));
    }
}
