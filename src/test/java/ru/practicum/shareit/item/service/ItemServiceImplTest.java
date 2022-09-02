package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.mappers.ItemMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private ItemMapper itemMapper;
    private ItemService itemService;
    private final LocalDateTime DATE_TIME = LocalDateTime.parse("2022-11-11T10:10:10");

    @BeforeEach
    void setItemService() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository,
                commentRepository, requestRepository, itemMapper, new ModelMapper());
    }

    UserDto createUserDto() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("NameDto");
        dto.setEmail("email@email.ru");
        return dto;
    }

    User createUser() {
        User user = new User();
        user.setId(1L);
        user.setName("NameDto");
        user.setEmail("email@email.ru");
        return user;
    }

    User createUserTwo() {
        User user = new User();
        user.setId(2L);
        user.setName("NameDtoTwo");
        user.setEmail("emailTwo@email.ru");
        return user;
    }

    ItemRequest createRequest() {
        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("DescriptionRequest");
        request.setRequestor(createUserTwo());
        request.setCreated(DATE_TIME);
        return request;
    }

    Item createItemNullCommentsNullRequest() {
        Item item = new Item();
        item.setId(1L);
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(createUser());
        return item;
    }

    ItemDto createItemDtoNullCommentsNullRequest() {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(createUserDto());
        return item;
    }

    Item createItemWithRequest() {
        Item item = new Item();
        item.setId(1L);
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(createUser());
        item.setRequest(createRequest());
        return item;
    }

    ItemDto createItemDtoWithRequest() {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("NameItem");
        item.setDescription("DescriptionItem");
        item.setAvailable(true);
        item.setOwner(createUserDto());
        item.setRequestId(1L);
        return item;
    }

    @Test
    void addItemNotRequest() {
        Item itemNotRequest = createItemNullCommentsNullRequest();
        ItemDto itemDtoNotRequest = createItemDtoNullCommentsNullRequest();
        ItemDto itemDtoNotRequestNotId = createItemDtoNullCommentsNullRequest();
        itemDtoNotRequestNotId.setId(null);
        ItemDto itemDtoWithRequestNotId = createItemDtoWithRequest();
        itemDtoWithRequestNotId.setId(null);
        User user = createUser();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(itemMapper.toItemEntity(user, itemDtoNotRequestNotId, null))
                .thenReturn(itemNotRequest);
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(itemNotRequest);
        Mockito.when(itemMapper.toItemDto(itemNotRequest))
                .thenReturn(itemDtoNotRequest);
        ItemDto actualItemDto = itemService.addItem(1L, itemDtoNotRequestNotId);
        assertEquals(itemDtoNotRequest, actualItemDto);
    }

    @Test
    void addItemWithRequest() {
        Item itemWithRequest = createItemWithRequest();
        ItemDto itemDtoWithRequest = createItemDtoWithRequest();
        ItemDto itemDtoWithRequestNotId = createItemDtoWithRequest();
        itemDtoWithRequestNotId.setId(null);
        ItemRequest request = createRequest();
        User user = createUser();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        Mockito.when(itemMapper.toItemEntity(user, itemDtoWithRequestNotId, request))
                .thenReturn(itemWithRequest);
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(itemWithRequest);
        Mockito.when(itemMapper.toItemDto(itemWithRequest))
                .thenReturn(itemDtoWithRequest);
        ItemDto actualItemDto = itemService.addItem(1L, itemDtoWithRequestNotId);
        assertEquals(itemDtoWithRequest, actualItemDto);
        assertThrows(NotFoundException.class, () -> itemService.addItem(2L, itemDtoWithRequestNotId));
    }

    @Test
    void addItemWithRequestBadIdRequest() {
        ItemDto itemDtoWithRequestNotId = createItemDtoWithRequest();
        itemDtoWithRequestNotId.setId(null);
        User user = createUser();
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(requestRepository.findById(1L)).thenThrow(new NotFoundException("Такого запроса нет"));
        assertThrows(NotFoundException.class, () -> itemService.addItem(1L, itemDtoWithRequestNotId));
    }

    @Test
    void changeItemAllOk() {
        Item expectedItem = createItemNullCommentsNullRequest();
        ItemDto itemDtoNullId = createItemDtoNullCommentsNullRequest();
        itemDtoNullId.setId(null);
        ItemDto expectedItemDto = createItemDtoNullCommentsNullRequest();
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(expectedItem));
        Mockito.when(itemMapper.toItemDto(Mockito.any(Item.class))).thenReturn(expectedItemDto);
        Mockito.when(itemRepository.save(Mockito.any(Item.class))).thenReturn(expectedItem);
        assertEquals(expectedItemDto, itemService.changeItem(1L, 1L, itemDtoNullId));
    }

    @Test
    void changeItemBadIdItem() {
        ItemDto itemDtoNullId = createItemDtoNullCommentsNullRequest();
        itemDtoNullId.setId(null);
        Mockito.when(itemRepository.findById(Mockito.any(Long.class)))
                .thenThrow(new NotFoundException("Нет такой вещи"));
        assertThrows(NotFoundException.class, () -> itemService.changeItem(1L, 1L, itemDtoNullId));
    }

    @Test
    void changeItemOtherOwner() {
        Item expectedItem = createItemNullCommentsNullRequest();
        ItemDto itemDtoNullId = createItemDtoNullCommentsNullRequest();
        itemDtoNullId.setId(null);
        Mockito.when(itemRepository.findById(1L)).thenReturn(Optional.of(expectedItem));
        assertThrows(NotFoundException.class, () -> itemService.changeItem(2L, 1L, itemDtoNullId));
    }

    @Test
    void deleteByUserIdAndItemId() {
        itemService.deleteByUserIdAndItemId(1L, 1L);
        Mockito.verify(itemRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }
}