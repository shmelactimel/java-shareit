package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto create(long userId, BookingCreateDto bookingCreateDto) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id == %d not found",
                        userId)));
        var item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("item with id == %d not found",
                        bookingCreateDto.getItemId())));
        if (!item.getAvailable()) {
            throw new AccessDeniedException(String.format("item with id == %d not available", item.getId()));
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("booker cannot be a owner");
        }
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                bookingCreateDto.getItemId(), bookingCreateDto.getStart(), bookingCreateDto.getEnd());
        if (!overlappingBookings.isEmpty()) {
            throw new AccessDeniedException(String.format("item with id == %d is already booked for the given period", item.getId()));
        }
        var booking = bookingMapper.dtoToBooking(bookingCreateDto, user, item);
        return bookingMapper.bookingToDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(long id, Long userId) {
        var booking = bookingRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException(String.format("booking with id == %d not found", id)));
        return bookingMapper.bookingToDtoResponse(booking);
    }

    @Override
    public BookingDto updateStatus(long id, Long ownerId, boolean approved) {
        var booking = bookingRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("booking with id == %d not found", id)));
        if (BookingStatus.APPROVED.equals(booking.getStatus())) {
            throw new AccessDeniedException("Status already approved");
        }
        if (approved && booking.getStart().isBefore(LocalDateTime.now())) {
            throw new AccessDeniedException("Cannot approve booking that starts in the past");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);
        return bookingMapper.bookingToDtoResponse(booking);
    }

    @Override
    public List<BookingDto> findAllForUser(Long bookerId, BookingState state) {
        userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id == %d not found", bookerId)));
        var sort = Sort.by("start").descending();
        List<Booking> result = Collections.emptyList();
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByBookerId(bookerId, sort);
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerCurrent(bookerId, LocalDateTime.now(), sort);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.REJECTED, sort);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerIdAndStatus(bookerId, BookingStatus.WAITING, sort);
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerIdAndStartAfter(bookerId, LocalDateTime.now(), sort);
                break;
            case PAST:
                result = bookingRepository.findAllByBookerIdAndEndBefore(bookerId, LocalDateTime.now(), sort);
                break;
        }
        return bookingMapper.bookingsToDtoResponse(result);
    }

    @Override
    public List<BookingDto> findAllForOwner(Long ownerId, BookingState state) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("user with id == %d not found", ownerId)));

        List<Booking> result = Collections.emptyList();
        var sort = Sort.by("start").descending();
        switch (state) {
            case ALL:
                result = bookingRepository.findAllByItemOwnerId(ownerId, sort);
                break;
            case CURRENT:
                result = bookingRepository.findAllByItemOwnerCurrent(ownerId, LocalDateTime.now(), sort);
                break;
            case REJECTED:
                result = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, sort);

                break;
            case WAITING:
                result = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, sort);
                break;
            case FUTURE:
                result = bookingRepository.findAllByItemOwnerIdAndStartAfter(ownerId, LocalDateTime.now(), sort);
                break;
            case PAST:
                result = bookingRepository.findAllByItemOwnerIdAndEndBefore(ownerId, LocalDateTime.now(), sort);
                break;
        }
        return bookingMapper.bookingsToDtoResponse(result);
    }
}