package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemClient;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemClient itemClient;
    private final LocalDateTime dateTimeNext = LocalDateTime.parse("2022-11-12T10:10:10");

    UserDto createUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("NameDto");
        dto.setEmail("email@email.ru");
        return dto;
    }

    ItemDto createItemDtoOne() {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("NameItemOne");
        item.setDescription("DescriptionItemOne");
        item.setAvailable(true);
        item.setOwner(createUserDto());
        return item;
    }

    CommentDto createCommentDto() {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("CommentDtoText");
        comment.setItem(createItemDtoOne());
        comment.setAuthorName("createUser.getName()");
        comment.setCreated(dateTimeNext.plusDays(1));
        return comment;
    }

    @Test
    void addCommentNotCommentText() throws Exception {
        CommentDto expectedCommentDto = createCommentDto();
        CommentDto commentDto = createCommentDto();
        commentDto.setId(null);
        commentDto.setText(" ");
        Mockito.when(itemClient.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(new ResponseEntity<>(expectedCommentDto, HttpStatus.OK));
        mockMvc.perform(post("/items/1/comment")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void findByUserIdBadUser() throws Exception {
        String textError = "Id не должен быть отрицательным";
        int from = 0;
        int size = 1;
        Mockito.when(itemClient.findByUserId(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(new ResponseEntity<>(new Object(), HttpStatus.OK));
        mockMvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", -1)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(textError, result.getResolvedException().getMessage()));
    }
}