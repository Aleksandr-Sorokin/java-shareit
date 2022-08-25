package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;


@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

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
    void getUser() throws Exception {
        /*UserDto userDto = createDto();
        UserDto expectedDto = createDto();
        expectedDto.setId(1L);
        Mockito.when(userService.addUser(userDto)).thenReturn(expectedDto);
        assertTrue(userDto.getId() == null);
        mockMvc.perform(post("/users"))
                .content(objectMapper.writeValueAsString(userDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType*/
    }

    @Test
    void addUser() {
    }

    @Test
    void changeUser() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void getAllUser() {
    }
}