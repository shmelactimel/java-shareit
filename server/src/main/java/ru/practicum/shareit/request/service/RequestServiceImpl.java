package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ErrorMessages;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemWithRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.dao.RequestRepository;
import ru.practicum.shareit.request.dto.RequestCreateDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestMapper requestMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public RequestDto create(long userId, RequestCreateDto requestCreateDto) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND.getFormatMessage(userId)));
        var rq = requestMapper.toModel(requestCreateDto, user, LocalDateTime.now());
        var request = requestRepository.save(rq);
        return requestMapper.toDto(request);
    }

    @Override
    @Transactional(readOnly = true)
    public RequestWithItemsDto findById(long userId, long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND.getFormatMessage(userId)));
        var request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.REQUEST_NOT_FOUND.getFormatMessage(requestId)));
        var items = itemRepository.findAllByRequestId(requestId).stream()
                .map(itemMapper::toItemWithRequestDto)
                .collect(Collectors.toList());
        return requestMapper.toRequestWithItemsDto(request, items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestWithItemsDto> findByUserId(long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND.getFormatMessage(userId)));
        var requests = requestRepository.findAllByUserId(userId, Sort.by("created").descending());
        return getItems(requests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestWithItemsDto> findAll(long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.USER_NOT_FOUND.getFormatMessage(userId)));
        List<Request> requests = requestRepository.findAllByUserIdNot(userId, pageable);
        return getItems(requests);
    }

    private List<RequestWithItemsDto> getItems(List<Request> requests) {
        var requestsId = requests.stream()
                .map(Request::getId)
                .collect(Collectors.toList());
        var items = itemRepository.findAllByRequestIdIn(requestsId)
                .stream()
                .map(itemMapper::toItemWithRequestDto)
                .collect(Collectors.groupingBy(ItemWithRequestDto::getRequestId, Collectors.toList()));

        return requests.stream()
                .map(r -> requestMapper.toRequestWithItemsDto(r, items.getOrDefault(r.getId(), Collections.emptyList())))
                .collect(Collectors.toList());
    }
}