package ru.practicum.shareit.requests.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.model.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.dto.RequestAllWithItemDto;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.service.mapper.RequestMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private RequestMapper requestMapper;
    private UserRepository userRepository;
    private ItemRepository itemRepository;
    private ItemRequestRepository requestRepository;
    private String responseNotUser = "Такого пользователя нет";

    public ItemRequestServiceImpl(RequestMapper requestMapper, UserRepository userRepository,
                                  ItemRepository itemRepository, ItemRequestRepository requestRepository) {
        this.requestMapper = requestMapper;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public ItemRequestDto addItemRequest(Long userId, ItemRequestDto requestDto) {
        requestDto.setCreated(LocalDateTime.now().withNano(0));
        User requestor = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(responseNotUser));
        ItemRequest itemRequest = requestMapper.toEntity(requestDto, requestor);
        return requestMapper.toDto(requestRepository.save(itemRequest));
    }

    @Override
    public List<RequestAllWithItemDto> getAllItemRequestWithResponse(Long requestorId) {
        userRepository.findById(requestorId).orElseThrow(() -> new NotFoundException(responseNotUser));
        List<ItemRequest> requests = requestRepository.findAllByRequestor(requestorId);
        return listRequestAllWithItemDto(requests);
    }

    @Override
    public List<RequestAllWithItemDto> getAllItemRequest(Long userId, Pageable pageable) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(responseNotUser));
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdIsNot(userId, pageable).toList();
        return listRequestAllWithItemDto(requests);
    }

    @Override
    public RequestAllWithItemDto getRequestByRequestId(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(responseNotUser));
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("В базе нет данных"));
        return requestMapper.toDto(request, itemRepository.findAllItemByRequest_Id(requestId));
    }

    private List<RequestAllWithItemDto> listRequestAllWithItemDto(List<ItemRequest> requests) {
        return requests.stream()
                .map(itemRequest -> requestMapper.toDto(itemRequest,
                        itemRepository.findAllItemByRequest_Id(itemRequest.getId())))
                .collect(Collectors.toList());
    }
}
