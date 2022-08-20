package ru.practicum.shareit.item.service.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.mappers.BookingMapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDtoId;
import ru.practicum.shareit.configuration.MapperUtil;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemDtoBooking;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.mappers.UserMapper;

import java.util.List;

@Component
public class ItemMapper {
    @Autowired
    private ModelMapper modelMapper;
    private UserMapper userMapper;
    private BookingMapping bookingMapping;
    private String textValidError = "Отсутствуют данные";

    public ItemMapper(UserMapper userMapper, BookingMapping bookingMapping) {
        this.userMapper = userMapper;
        this.bookingMapping = bookingMapping;
    }

    public Item toItemEntity(User user, ItemDto itemDto) {
        if (itemDto == null) {
            throw new ValidationException(textValidError);
        } else {
            Item item = modelMapper.map(itemDto, Item.class);
            item.setOwner(user);
            return item;
        }
    }

    public Item toItemEntity(User user, ItemDtoBooking itemDto) {
        if (itemDto == null) {
            throw new ValidationException(textValidError);
        } else {
            Item item = modelMapper.map(itemDto, Item.class);
            item.setOwner(user);
            return item;
        }
    }

    public ItemDto toItemDto(Item item) {
        if (item == null) {
            throw new ValidationException(textValidError);
        } else {
            User user = item.getOwner();
            ItemDto itemDto = modelMapper.map(item, ItemDto.class);
            itemDto.setOwner(userMapper.toUserDto(user));
            if (item.getComments() != null) {
                itemDto.setComments(MapperUtil.convertList(item.getComments(), this::toCommentDto));
            }
            return itemDto;
        }
    }

    public CommentDto toCommentDto(Comment comment) {
        User author = comment.getAuthor();
        Item item = comment.getItem();
        CommentDto commentDto = modelMapper.map(comment, CommentDto.class);
        commentDto.setItem(toItemDto(item));
        commentDto.setAuthorName(author.getName());
        return commentDto;
    }
    
    public Comment toComment(User author, Item item, CommentDto commentDto) {
        Comment comment = modelMapper.map(commentDto, Comment.class);
        comment.setAuthor(author);
        comment.setItem(item);
        return comment;
    }

    public ItemDtoBooking toItemDtoBooking(Item item, User viewUser, Booking last,
                                           Booking next, List<Comment> comments) {
        if (item == null) {
            throw new ValidationException(textValidError);
        } else {
            ItemDtoBooking itemDtoBooking = modelMapper.map(item, ItemDtoBooking.class);
            itemDtoBooking.setOwner(userMapper.toUserDto(item.getOwner()));
            itemDtoBooking.setComments(MapperUtil.convertList(comments, this::toCommentDto));
            if (viewUser.equals(item.getOwner())) {
                itemDtoBooking.setLastBooking(last == null ? null : modelMapper.map(last, BookingDtoId.class));
                itemDtoBooking.setNextBooking(next == null ? null : modelMapper.map(next, BookingDtoId.class));
            } else {
                itemDtoBooking.setLastBooking(null);
                itemDtoBooking.setNextBooking(null);
            }
            return itemDtoBooking;
        }
    }
}
