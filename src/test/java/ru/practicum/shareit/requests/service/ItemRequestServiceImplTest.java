package ru.practicum.shareit.requests.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.configuration.PageHandlerRequest;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemForRequestDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.requests.model.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.dto.RequestAllWithItemDto;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemRequestServiceImplTest {
    private final ItemRequestService requestService;
    private final UserService userService;
    private final ItemService itemService;

    @BeforeEach
    void addData() {
        ItemDto itemDtoOne = createItemDtoOne();
        ItemDto itemDtoTwo = createItemDtoTwo();
        userService.addUser(createUserDto());
        userService.addUser(createUserDtoTwo());
        userService.addUser(createUserDtoThree());
        itemService.addItem(1L, itemDtoOne);
        itemService.addItem(1L, itemDtoTwo);
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        ItemRequestDto requestDto = createItemRequestDto();
        requestDto.setId(null);
        requestDto.setCreated(localDateTime);
        requestService.addItemRequest(2L, requestDto);
    }

    UserDto createUserDtoThree() {
        UserDto dto = new UserDto();
        dto.setId(3L);
        dto.setName("NameDtoThree");
        dto.setEmail("emailThree@email.ru");
        return dto;
    }

    UserDto createUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("NameDto");
        dto.setEmail("email@email.ru");
        return dto;
    }

    UserDto createUserDtoTwo() {
        UserDto user = new UserDto();
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

    ItemDto createItemDtoTwo() {
        ItemDto item = new ItemDto();
        item.setId(2L);
        item.setName("NameItemTwo");
        item.setDescription("DescriptionItemTwo");
        item.setAvailable(true);
        item.setOwner(createUserDto());
        return item;
    }

    ItemRequestDto createItemRequestDto() {
        ItemRequestDto request = new ItemRequestDto();
        request.setId(1L);
        request.setDescription("requestDescription");
        request.setRequestor(createUserDtoTwo());
        return request;
    }

    ItemForRequestDto createItemForRequestDto() {
        ItemForRequestDto item = new ItemForRequestDto();
        item.setId(1L);
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwnerId(1L);
        item.setRequestId(1L);
        return item;
    }

    RequestAllWithItemDto createRequestAllWithItemDto() {
        RequestAllWithItemDto request = new RequestAllWithItemDto();
        request.setId(1L);
        request.setDescription("requestDescription");
        request.setItems(List.of(createItemForRequestDto()));
        return request;
    }

    @Test
    void addItemRequest() {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        ItemRequestDto requestDto = createItemRequestDto();
        requestDto.setId(null);
        requestDto.setCreated(localDateTime);
        ItemRequestDto expected = createItemRequestDto();
        expected.setCreated(localDateTime);
        expected.setId(2L);
        ItemRequestDto actual = requestService.addItemRequest(2L, requestDto);
        assertEquals(expected, actual);
    }

    @Test
    void getAllItemRequestWithResponse() {
        RequestAllWithItemDto requestAllNotItem = createRequestAllWithItemDto();
        requestAllNotItem.setItems(new ArrayList<>());
        requestAllNotItem.setCreated(LocalDateTime.now().withNano(0));
        List<RequestAllWithItemDto> expected = List.of(requestAllNotItem);
        assertEquals(expected, requestService.getAllItemRequestWithResponse(2L));
    }

    @Test
    void getAllItemRequest() {
        Pageable pageable = PageHandlerRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "created"));
        RequestAllWithItemDto requestAllNotItem = createRequestAllWithItemDto();
        requestAllNotItem.setItems(new ArrayList<>());
        requestAllNotItem.setCreated(LocalDateTime.now().withNano(0));
        List<RequestAllWithItemDto> expected = List.of(requestAllNotItem);
        assertEquals(expected, requestService.getAllItemRequest(1L, pageable));
    }

    @Test
    void getRequestByRequestId() {
        RequestAllWithItemDto requestAllNotItem = createRequestAllWithItemDto();
        requestAllNotItem.setItems(new ArrayList<>());
        requestAllNotItem.setCreated(LocalDateTime.now().withNano(0));
        assertEquals(requestAllNotItem, requestService.getRequestByRequestId(3L, 1L));
        assertThrows(NotFoundException.class, () -> requestService.getRequestByRequestId(3L, 2L));
    }
}