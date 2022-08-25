package ru.practicum.shareit.requests.service.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.configuration.MapperUtil;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemForRequestDto;
import ru.practicum.shareit.item.service.mappers.ItemMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.model.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.dto.RequestAllWithItemDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.mappers.UserMapper;

import java.util.List;

@Component
public class RequestMapper {
    private ModelMapper mapper;
    private UserMapper userMapper;
    private ItemMapper itemMapper;

    public RequestMapper(ModelMapper mapper, UserMapper userMapper, ItemMapper itemMapper) {
        this.mapper = mapper;
        this.userMapper = userMapper;
        this.itemMapper = itemMapper;
    }

    public ItemRequest toEntity(ItemRequestDto requestDto, User requestor) {
        ItemRequest entity = mapper.map(requestDto, ItemRequest.class);
        entity.setRequestor(requestor);
        return entity;
    }

    public ItemRequestDto toDto(ItemRequest itemRequest) {
        ItemRequestDto dto = mapper.map(itemRequest, ItemRequestDto.class);
        dto.setRequestor(userMapper.toUserDto(itemRequest.getRequestor()));
        return dto;
    }

    public RequestAllWithItemDto toDto(ItemRequest itemRequest, List<Item> items) {
        RequestAllWithItemDto dto = mapper.map(itemRequest, RequestAllWithItemDto.class);
        List<ItemForRequestDto> itemsDto = MapperUtil.convertList(items, item -> itemMapper.toDtoItemForRequest(item));
        dto.setItems(itemsDto);
        return dto;
    }
}
