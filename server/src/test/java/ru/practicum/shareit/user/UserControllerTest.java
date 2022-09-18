package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        UserDto userDto = createDto();
        UserDto expectedDto = createDto();
        expectedDto.setId(1L);
        Mockito.when(userService.findUserById(anyLong())).thenReturn(expectedDto);
        mockMvc.perform(get("/users/" + 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void addUser() throws Exception {
        UserDto userDto = createDto();
        UserDto expectedDto = createDto();
        expectedDto.setId(1L);
        Mockito.when(userService.addUser(userDto)).thenReturn(expectedDto);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(expectedDto.getName())))
                .andExpect(jsonPath("$.email", is(expectedDto.getEmail())));
    }

    @Test
    void changeUser() throws Exception {
        Long userId = 1L;
        UserDto userDto = createDto();
        UserDto expectedDto = createDto();
        expectedDto.setId(userId);
        Mockito.when(userService.changeUser(userId, userDto)).thenReturn(expectedDto);
        mockMvc.perform(patch("/users/" + userId)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(expectedDto.getName())))
                .andExpect(jsonPath("$.email", is(expectedDto.getEmail())));
    }

    @Test
    void deleteUser() throws Exception {
        mockMvc.perform(delete("/users/" + anyLong())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllUser() throws Exception {
        UserDto userDtoOne = createDto();
        UserDto userDtoTwo = createDtoTwo();
        UserDto userDtoThree = createDtoThree();
        userDtoOne.setId(1L);
        userDtoTwo.setId(2L);
        userDtoThree.setId(3L);
        Mockito.when(userService.findAll()).thenReturn(List.of(userDtoOne, userDtoTwo, userDtoThree));
        mockMvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$[0].id", is(userDtoOne.getId()), Long.class))
                .andExpect(jsonPath("$[1].id", is(userDtoTwo.getId()), Long.class))
                .andExpect(jsonPath("$[2].id", is(userDtoThree.getId()), Long.class));
    }
}