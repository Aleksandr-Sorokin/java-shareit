package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.configuration.MapperUtil;
import ru.practicum.shareit.exeptions.DuplicateEmail;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto addUser(User user) {
        if (userRepository.findAll().stream()
                .filter(o -> o.getEmail().equals(user.getEmail()))
                .findAny().isPresent()) {
            throw new DuplicateEmail("Такой Email уже существует у другого пользователя");
        }
        userRepository.addUser(user);
        return convertToUserDto(user);
    }

    @Override
    public UserDto changeUser(long userId, UserDto userDto) {
        User user = userRepository.findUserById(userId);
        userDto.setId(userId);
        checkDuplicateEmail(userId, userDto.getEmail());
        modelMapper.map(userDto, user);
        userRepository.changeUser(userId, user);
        return convertToUserDto(user);
    }

    @Override
    public UserDto findUserById(long userId) {
        return convertToUserDto(userRepository.findUserById(userId));
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        return MapperUtil.convertList(users, this::convertToUserDto);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteUser(userId);
    }

    private UserDto convertToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    private void checkDuplicateEmail(long userId, String email) {
        if (userRepository.findAll().stream()
                .filter(n -> n.getId() != userId)
                .filter(o -> o.getEmail().equals(email))
                .findAny().isPresent()) {
            throw new DuplicateEmail("Такой Email уже существует");
        }
    }
}
