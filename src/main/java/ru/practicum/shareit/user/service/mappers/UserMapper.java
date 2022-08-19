package ru.practicum.shareit.user.service.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

@Component
public class UserMapper {
    @Autowired
    private ModelMapper modelMapper;
    private String errorValidText = "Отсутствуют данные";

    public User toUserEntity(UserDto userDto) {
        if (userDto == null) {
            throw new ValidationException(errorValidText);
        } else {
            return modelMapper.map(userDto, User.class);
        }
    }

    public UserDto toUserDto(User user) {
        if (user == null) {
            throw new ValidationException(errorValidText);
        } else {
            return modelMapper.map(user, UserDto.class);
        }
    }
}
