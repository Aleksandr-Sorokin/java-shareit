package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDto;
import ru.practicum.shareit.booking.model.dto.BookingDtoId;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.configuration.MapperUtil;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public BookingServiceImpl(ItemRepository itemRepository, BookingRepository bookingRepository,
                              UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingDto addBooking(long bookerId, BookingDtoId bookingDtoId) {
        checkItem(bookerId, bookingDtoId.getItemId());
        checkValidTime(bookingDtoId);
        bookingDtoId.setBookerId(bookerId);
        Booking booking = modelMapper.map(bookingDtoId, Booking.class);
        booking.setStatus(Status.WAITING);
        checkAvailable(booking);
        return modelMapper.map(bookingRepository.save(booking), BookingDto.class);
    }

    @Override
    public BookingDto approvedBooking(long ownerId, boolean approved, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));
        if (!booking.getStatus().equals(Status.WAITING)) throw new ValidationException("Бронь уже подтверждена");
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = booking.getItem();
        if (!item.getOwner().equals(owner)) throw new NotFoundException("Вы не владелец вещи");
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        bookingRepository.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    public BookingDto getBookingById(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        BookingDto bookingDto = modelMapper.map(booking, BookingDto.class);
        if (user.equals(booking.getBooker()) || user.equals(booking.getItem().getOwner())) {
            return bookingDto;
        } else {
            throw new NotFoundException("Вы не владелец или пользователь вещи");
        }
    }

    @Override
    public List<BookingDto> getAllBookingByBookerId(long bookerId, State state) {
        checkUser(bookerId);
        switch (state) {
            case ALL:
                List<BookingDto> bookingDtoList = MapperUtil.convertList(bookingRepository
                                .findAllByBooker_IdOrderByStartDesc(bookerId),
                        this::convertToDto);
                return bookingDtoList;
            case PAST:
                return MapperUtil.convertList(bookingRepository
                                .findAllByBookerPast(bookerId, LocalDateTime.now()),
                        this::convertToDto);
            case CURRENT:
                return MapperUtil.convertList(bookingRepository
                                .findAllByBookerCurrent(bookerId, LocalDateTime.now()),
                        this::convertToDto);
            case FUTURE:
                return MapperUtil.convertList(bookingRepository
                                .findAllByBookerFuture(bookerId, LocalDateTime.now()),
                        this::convertToDto);
            case WAITING:
                return MapperUtil.convertList(bookingRepository
                                .findAllByBookerAndStatus(bookerId, Status.WAITING),
                        this::convertToDto);
            case REJECTED:
                return MapperUtil.convertList(bookingRepository
                                .findAllByBookerAndStatus(bookerId, Status.REJECTED),
                        this::convertToDto);
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private BookingDto convertToDto(Booking booking) {
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    public List<BookingDto> getAllBookingByOwnerId(long ownerId, State state) {
        checkUser(ownerId);
        switch (state) {
            case ALL:
                List<BookingDto> bookingDtoList = MapperUtil.convertList(bookingRepository
                                .findAllByItem_Owner_IdOrderByStartDesc(ownerId),
                        this::convertToDto);
                return bookingDtoList;
            case PAST:
                return MapperUtil.convertList(bookingRepository
                                .findAllByOwnerPast(ownerId, LocalDateTime.now()),
                        this::convertToDto);
            case CURRENT:
                return MapperUtil.convertList(bookingRepository
                                .findAllByOwnerCurrent(ownerId, LocalDateTime.now()),
                        this::convertToDto);
            case FUTURE:
                return MapperUtil.convertList(bookingRepository
                                .findAllByOwnerFuture(ownerId, LocalDateTime.now()),
                        this::convertToDto);
            case WAITING:
                return MapperUtil.convertList(bookingRepository
                                .findAllByOwnerAndStatus(ownerId, Status.WAITING),
                        this::convertToDto);
            case REJECTED:
                return MapperUtil.convertList(bookingRepository
                                .findAllByOwnerAndStatus(ownerId, Status.REJECTED),
                        this::convertToDto);
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    private void checkItem(Long userId, Long itemId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        if (user.equals(item.getOwner())) throw new NotFoundException("Владелец не может арендовать у себя");
    }

    private User checkUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private void checkValidTime(BookingDtoId bookingDtoId) {
        LocalDateTime startTime = bookingDtoId.getStart();
        LocalDateTime endTime = bookingDtoId.getEnd();
        if (endTime.isBefore(LocalDateTime.now())) throw new ValidationException("Время окончания не корректно");
        if (startTime.isAfter(endTime)) throw new ValidationException("Время окончания раньше начала");
        if (startTime.isBefore(LocalDateTime.now())) throw new ValidationException("Время начала не корректно");
    }

    private void checkAvailable(Booking booking) {
        if (!booking.getItem().getAvailable()) throw new ValidationException("Вешь занята");
    }
}
