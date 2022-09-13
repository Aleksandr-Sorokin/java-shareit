package ru.practicum.shareit.booking.mappers;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingDtoId;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.mappers.UserMapper;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Component
public class BookingMapping {
    private ModelMapper modelMapper;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private UserMapper userMapper;

    public BookingMapping(ModelMapper modelMapper, UserRepository userRepository, ItemRepository itemRepository, UserMapper userMapper) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.userMapper = userMapper;
    }

    @PostConstruct
    public void setupMapper() {
        modelMapper.createTypeMap(Booking.class, BookingDtoId.class)
                .addMappings(mapping -> mapping.skip(BookingDtoId::setBookerId))
                .addMappings(mapping -> mapping.skip(BookingDtoId::setItemId))
                .setPostConverter(toBookingDtoIdConverter());

        modelMapper.createTypeMap(BookingDtoId.class, Booking.class)
                .addMappings(mapping -> mapping.skip(Booking::setBooker))
                .addMappings(mapping -> mapping.skip(Booking::setItem))
                .setPostConverter(toBookingConverter());
    }

    public void mapSpecificField(Booking source, BookingDtoId destination) {
        destination.setBookerId(Objects.isNull(source)
                || Objects.isNull(source.getId()) ? null : source.getBooker().getId());
        destination.setItemId(Objects.isNull(source)
                || Objects.isNull(source.getId()) ? null : source.getItem().getId());
    }

    public void mapSpecificField(BookingDtoId source, Booking destination) {
        destination.setBooker(userRepository.findById(source.getBookerId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден")));
        destination.setItem(itemRepository.findById(source.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена")));
    }

    public Converter<BookingDtoId, Booking> toBookingConverter() {
        return context -> {
            BookingDtoId source = context.getSource();
            Booking destination = context.getDestination();
            mapSpecificField(source, destination);
            return context.getDestination();
        };
    }

    public Converter<Booking, BookingDtoId> toBookingDtoIdConverter() {
        return context -> {
            Booking source = context.getSource();
            BookingDtoId destination = context.getDestination();
            mapSpecificField(source, destination);
            return context.getDestination();
        };
    }
}
