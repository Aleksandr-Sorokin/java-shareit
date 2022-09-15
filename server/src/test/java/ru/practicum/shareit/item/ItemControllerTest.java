package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoBooking;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    private final LocalDateTime dateTimeNext = LocalDateTime.parse("2022-11-12T10:10:10");

    UserDto createUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("NameDto");
        dto.setEmail("email@email.ru");
        return dto;
    }

    User createUserTwo() {
        User user = new User();
        user.setId(2L);
        user.setName("NameDtoTwo");
        user.setEmail("emailTwo@email.ru");
        return user;
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

    ItemDtoBooking createItemDtoBooking() {
        ItemDtoBooking dtoBooking = new ItemDtoBooking();
        dtoBooking.setId(1L);
        dtoBooking.setName("NameItemOne");
        dtoBooking.setDescription("DescriptionItemOne");
        dtoBooking.setAvailable(true);
        dtoBooking.setOwner(createUserDto());
        dtoBooking.setComments(new ArrayList<>());
        return dtoBooking;
    }

    CommentDto createCommentDto() {
        CommentDto comment = new CommentDto();
        comment.setId(1L);
        comment.setText("CommentDtoText");
        comment.setItem(createItemDtoOne());
        comment.setAuthorName(createUserTwo().getName());
        comment.setCreated(dateTimeNext.plusDays(1));
        return comment;
    }

    @Test
    void findItemById() throws Exception {
        ItemDtoBooking itemDtoBooking = createItemDtoBooking();
        Mockito.when(itemService.findItemById(1L, 1L)).thenReturn(itemDtoBooking);
        mockMvc.perform(get("/items/" + 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoBooking.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoBooking.getName())));
    }

    @Test
    void findItemByIdZeroUser() throws Exception {
        Mockito.when(itemService.findItemById(1L, 0L))
                .thenThrow(new ValidationException("Id не должен быть отрицательным"));
        mockMvc.perform(get("/items/" + 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 0))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findItemByIdZeroItem() throws Exception {
        Mockito.when(itemService.findItemById(0L, 1L))
                .thenThrow(new ValidationException("Id не должен быть отрицательным"));
        mockMvc.perform(get("/items/" + 0)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchItemAllOk() throws Exception {
        String text = "NameItem";
        int from = 0;
        int size = 1;
        List<ItemDto> expected = List.of(createItemDtoOne());
        Mockito.when(itemService.searchItem(Mockito.any(), Mockito.any())).thenReturn(expected);
        mockMvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void searchItemEmptyText() throws Exception {
        String text = "";
        int from = 0;
        int size = 1;
        Mockito.when(itemService.searchItem(Mockito.any(), Mockito.any())).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ArrayList<>())));
    }

    @Test
    void searchItemNotPage() throws Exception {
        String text = "NameItem";
        List<ItemDto> expected = List.of(createItemDtoOne());
        Mockito.when(itemService.searchItem(Mockito.any(), Mockito.any())).thenReturn(expected);
        mockMvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", text)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void searchItemBadPage() throws Exception {
        String text = "NameItem";
        int from = -1;
        int size = 0;
        List<ItemDto> expected = List.of(createItemDtoOne());
        Mockito.when(itemService.searchItem(Mockito.any(), Mockito.any())).thenReturn(expected);
        mockMvc.perform(get("/items/search")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", text)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void findByUserIdAllOk() throws Exception {
        int from = 0;
        int size = 1;
        List<ItemDtoBooking> expected = List.of(createItemDtoBooking());
        Mockito.when(itemService.findByUserId(Mockito.anyLong(), Mockito.any())).thenReturn(expected);
        mockMvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void findByUserIdBadUser() throws Exception {
        String textError = "Id не должен быть отрицательным";
        int from = 0;
        int size = 1;
        List<ItemDtoBooking> expected = List.of(createItemDtoBooking());
        Mockito.when(itemService.findByUserId(Mockito.anyLong(), Mockito.any())).thenReturn(expected);
        mockMvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", -1)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                )
                .andExpect(status().isOk());
    }

    @Test
    void findByUserIdBadParamPage() throws Exception {
        String textError = "Не корректные данные";
        int from = -1;
        int size = 0;
        List<ItemDtoBooking> expected = List.of(createItemDtoBooking());
        Mockito.when(itemService.findByUserId(Mockito.anyLong(), Mockito.any())).thenReturn(expected);
        mockMvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", String.valueOf(from))
                        .param("size", String.valueOf(size))
                )
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(textError, result.getResolvedException().getMessage()));
    }

    @Test
    void addItem() throws Exception {
        ItemDto itemDto = createItemDtoOne();
        itemDto.setId(null);
        ItemDto expectedItemDto = createItemDtoOne();
        Mockito.when(itemService.addItem(Mockito.anyLong(), Mockito.any())).thenReturn(expectedItemDto);
        mockMvc.perform(post("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedItemDto)));
    }

    @Test
    void addItemNull() throws Exception {
        ItemDto itemDto = createItemDtoOne();
        itemDto.setId(null);
        ItemDto expectedItemDto = createItemDtoOne();
        Mockito.when(itemService.addItem(Mockito.anyLong(), Mockito.any())).thenReturn(expectedItemDto);
        mockMvc.perform(post("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeItem() throws Exception {
        ItemDto itemDto = createItemDtoOne();
        itemDto.setId(null);
        ItemDto expectedItemDto = createItemDtoOne();
        Mockito.when(itemService.changeItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(expectedItemDto);
        mockMvc.perform(patch("/items/" + 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedItemDto.getId()), Long.class));
    }

    @Test
    void changeItemNotBody() throws Exception {
        ItemDto itemDto = createItemDtoOne();
        itemDto.setId(null);
        ItemDto expectedItemDto = createItemDtoOne();
        Mockito.when(itemService.changeItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(expectedItemDto);
        mockMvc.perform(patch("/items/" + 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteByUserIdAndItemId() throws Exception {
        mockMvc.perform(delete("/items/" + 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                )
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {
        CommentDto expectedCommentDto = createCommentDto();
        CommentDto commentDto = createCommentDto();
        commentDto.setId(null);
        Mockito.when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(expectedCommentDto);
        mockMvc.perform(post("/items/1/comment")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                )
                .andExpect(status().isOk());
    }

    @Test
    void addCommentNotCommentText() throws Exception {
        CommentDto expectedCommentDto = createCommentDto();
        CommentDto commentDto = createCommentDto();
        commentDto.setId(null);
        commentDto.setText(" ");
        Mockito.when(itemService.addComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any()))
                .thenReturn(expectedCommentDto);
        mockMvc.perform(post("/items/1/comment")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                )
                .andExpect(status().isOk());
    }
}