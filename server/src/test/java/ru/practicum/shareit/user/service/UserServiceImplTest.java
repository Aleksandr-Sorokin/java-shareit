package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exeptions.DuplicateEmail;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplTest {
    private final UserService userService;

    UserDto createDto() {
        UserDto dto = new UserDto();
        dto.setName("NameDto");
        dto.setEmail("email@email.ru");
        return dto;
    }

    UserDto createDtoTwo() {
        UserDto dto = new UserDto();
        dto.setName("NameDtoTwo");
        dto.setEmail("email@emailTwo.ru");
        return dto;
    }

    UserDto createDtoThree() {
        UserDto dto = new UserDto();
        dto.setName("NameDtoThree");
        dto.setEmail("email@emailThree.ru");
        return dto;
    }

    @Test
    void addUser() {
        UserDto userDto = createDto();
        UserDto expectedDto = createDto();
        expectedDto.setId(1L);
        UserDto actualDto = userService.addUser(userDto);
        assertEquals(expectedDto, actualDto);
        assertThrows(ValidationException.class, () -> userService.addUser(null));
    }

    @Test
    void changeUser() {
        UserDto firstUser = createDto();
        UserDto secondUser = createDtoTwo();
        UserDto threeUser = createDtoThree();
        userService.addUser(firstUser);   //1
        userService.addUser(secondUser);  //2
        UserDto expectedDto = createDtoThree();
        expectedDto.setId(1L);
        assertThrows(DuplicateEmail.class, () -> userService.changeUser(1L, secondUser));
        assertThrows(NotFoundException.class, () -> userService.changeUser(99L, secondUser));
        UserDto actualDto = userService.changeUser(1, threeUser);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void findUserById() {
        UserDto firstUser = createDto();
        UserDto secondUser = createDtoTwo();
        userService.addUser(firstUser);   //1
        userService.addUser(secondUser);  //2
        UserDto expectedDto = createDtoTwo();
        expectedDto.setId(2L);
        assertThrows(NotFoundException.class, () -> userService.findUserById(99L));
        UserDto actualDto = userService.findUserById(2L);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void findAll() {
        UserDto firstUser = createDto();
        UserDto secondUser = createDtoTwo();
        UserDto threeUser = createDtoThree();
        List<UserDto> expected = new ArrayList<>();
        expected.add(userService.addUser(firstUser));   //1
        expected.add(userService.addUser(secondUser));  //2
        expected.add(userService.addUser(threeUser));   //3
        assertTrue(userService.findAll().size() == expected.size());
        assertEquals(expected, userService.findAll());
    }

    @Test
    void deleteUser() {
        UserDto firstUser = createDto();
        UserDto secondUser = createDtoTwo();
        UserDto threeUser = createDtoThree();
        UserDto first = userService.addUser(firstUser);
        UserDto second = userService.addUser(secondUser);
        UserDto three = userService.addUser(threeUser);
        List<UserDto> expected = new ArrayList<>();
        expected.add(first);   //1
        expected.add(second);  //2
        expected.add(three);   //3
        assertTrue(userService.findUserById(2L).equals(second));
        assertTrue(userService.findAll().size() == expected.size());
        userService.deleteUser(2L);
        assertThrows(NotFoundException.class, () -> userService.findUserById(2L));
        assertTrue(userService.findAll().size() == expected.size() - 1);
    }
}