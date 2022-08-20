package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.configuration.MapperUtil;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoBooking;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.mappers.ItemMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemMapper itemMapper;
    @Autowired
    private ModelMapper modelMapper;

    public ItemServiceImpl(ItemRepository itemRepository,
                           UserRepository userRepository, BookingRepository bookingRepository,
                           CommentRepository commentRepository, ItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemMapper = itemMapper;
    }

    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Такого пользователя нет"));
        Item item = itemMapper.toItemEntity(user, itemDto);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto changeItem(long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Нет такой вещи"));
        if (item.getOwner().getId() != userId) throw new NotFoundException("Вы не являетесь пользователем этой вещи");
        itemDto.setId(itemId);
        modelMapper.map(itemDto, item);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        String searchText = text.toLowerCase();
        List<Item> items = itemRepository.search(searchText);
        return MapperUtil.convertList(items, item -> itemMapper.toItemDto(item));
    }

    @Override
    public ItemDtoBooking findItemById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Нет такой вещи"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет"));
        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
        Booking last = bookingRepository.findFirstByItem_IdAndAndEndBeforeOrderByEndDesc(item.getId(), LocalDateTime.now());
        Booking next = bookingRepository.findFirstByItem_IdAndStartAfterOrderByStartAsc(item.getId(), LocalDateTime.now());
        return itemMapper.toItemDtoBooking(item, user, last, next, comments);
    }

    private Booking lastBooking(Long itemId) {
        return bookingRepository.findFirstByItem_IdAndAndEndBeforeOrderByEndDesc(itemId, LocalDateTime.now());
    }

    private Booking nextBooking(Long itemId) {
        return bookingRepository.findFirstByItem_IdAndStartAfterOrderByStartAsc(itemId, LocalDateTime.now());
    }

    @Override
    public List<ItemDtoBooking> findByUserId(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет"));
        List<Item> items = itemRepository.findAllByOwner_Id(userId);
        items.stream().map(item -> {
                    item.setComments(commentRepository.findAllByItem_Id(item.getId()));
                    return item;
                })
                .collect(Collectors.toList());
        return MapperUtil.convertList(items,
                item -> itemMapper.toItemDtoBooking(item, user, lastBooking(item.getId()),
                        nextBooking(item.getId()), item.getComments()));
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Нет такой вещи"));
        User author = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет"));
        Booking booking = bookingRepository.findFirstByItem_IdAndBooker_Id(itemId, userId);
        if (booking == null
                || commentDto.getCreated().isBefore(booking.getStart())
                || booking.getStatus().equals(Status.REJECTED)
                || author.equals(item.getOwner())) throw new ValidationException("Вы не брали в аренду эту вещь");
        Comment comment = commentRepository.save(itemMapper.toComment(author, item, commentDto));
        return itemMapper.toCommentDto(comment);
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        itemRepository.deleteById(itemId);
    }
}
