package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.configuration.MapperUtil;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService{
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        Item item = convertToItem(userId, itemDto);
        return convertToItemDto(itemRepository.addItem(userId, item));
    }

    @Override
    public ItemDto changeItem(long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.findItemById(itemId);
        if (item.getOwner().getId() != userId) throw new NotFoundException("Вы не являетесь пользователем этой вещи");
        itemDto.setId(itemId);
        modelMapper.map(itemDto, item);
        return convertToItemDto(itemRepository.changeItem(userId, itemId, item));
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        List<Item> items = itemRepository.searchItem(text);
        return MapperUtil.convertList(items, this::convertToItemDto);
    }

    @Override
    public ItemDto findItemById(long itemId) {
        return convertToItemDto(itemRepository.findItemById(itemId));
    }

    @Override
    public List<ItemDto> findByUserId(long userId) {
        List<Item> items = itemRepository.findByUserId(userId);
        return MapperUtil.convertList(items, this::convertToItemDto);
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        itemRepository.deleteByUserIdAndItemId(userId, itemId);
    }

    private ItemDto convertToItemDto(Item item){
        User user = item.getOwner();
        UserDto userDto = modelMapper.map(user, UserDto.class);
        ItemDto itemDto = modelMapper.map(item, ItemDto.class);
        itemDto.setOwner(userDto);
        return itemDto;
    }

    private Item convertToItem(long userId, ItemDto itemDto){
        User user = userRepository.findUserById(userId);
        Item item = modelMapper.map(itemDto, Item.class);
        item.setOwner(user);
        return item;
    }
}
