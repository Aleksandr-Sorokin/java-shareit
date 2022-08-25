package ru.practicum.shareit.user.service.mappers;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserMapperTest {
    UserMapper userMapper = new UserMapper(new ModelMapper());

    UserDto createDto() {
        UserDto dto = new UserDto();
        dto.setName("NameDto");
        dto.setEmail("email@email.ru");
        return dto;
    }

    User createUser() {
        User user = new User();
        user.setName("NameDto");
        user.setEmail("email@email.ru");
        return user;
    }

    @Test
    void toUserEntity() {
        UserDto dto = createDto();
        User user = createUser();
        User userEntity = userMapper.toUserEntity(dto);
        assertEquals(userEntity, user);
        assertThrows(ValidationException.class, () -> userMapper.toUserEntity(null));
    }

    @Test
    void toUserDto() {
        UserDto dto = createDto();
        dto.setId(1L);
        User user = createUser();
        user.setId(1L);
        UserDto userDto = userMapper.toUserDto(user);
        assertEquals(dto, userDto);
        assertThrows(ValidationException.class, () -> userMapper.toUserDto(null));
    }
}