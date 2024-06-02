package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingShort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking as b " +
            "where b.id = ?1 and (b.booker.id = ?2 or b.item.owner.id = ?2)")
    @EntityGraph("booking-graph")
    Optional<Booking> findByIdAndUserId(long id, long userId);

    @EntityGraph("booking-graph")
    Optional<Booking> findByIdAndItemOwnerId(long id, long ownerId);

    @EntityGraph("booking-graph")
    List<Booking> findAllByBookerId(long bookerId, Pageable pageable);

    @EntityGraph("booking-graph")
    List<Booking> findAllByBookerIdAndStatus(long bookerId, BookingStatus status, Pageable pageable);

    @EntityGraph("booking-graph")
    List<Booking> findAllByBookerIdAndStartAfter(long bookerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking as b " +
            "where b.booker.id = ?1 and (?2 between b.start and b.end) ")
    @EntityGraph("booking-graph")
    List<Booking> findAllByBookerCurrent(long bookerId, LocalDateTime date, Pageable pageable);

    @EntityGraph("booking-graph")
    List<Booking> findAllByBookerIdAndEndBefore(long bookerId, LocalDateTime date, Pageable pageable);

    @EntityGraph("booking-graph")
    List<Booking> findAllByItemOwnerId(long bookerId, Pageable pageable);

    @EntityGraph("booking-graph")
    List<Booking> findAllByItemOwnerIdAndStatus(long bookerId, BookingStatus status, Pageable pageable);

    @EntityGraph("booking-graph")
    List<Booking> findAllByItemOwnerIdAndStartAfter(long bookerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking as b " +
            "where b.item.owner.id = ?1 and (?2 between b.start and b.end) ")
    @EntityGraph("booking-graph")
    List<Booking> findAllByItemOwnerCurrent(long bookerId, LocalDateTime date, Pageable pageable);

    @EntityGraph("booking-graph")
    List<Booking> findAllByItemOwnerIdAndEndBefore(long bookerId, LocalDateTime date, Pageable pageable);

    @Query("select b from Booking b where b.item.id in ?1 and b.status = 'APPROVED'")
    List<Booking> findAllBookingsByItemIdIn(List<Long> items, Sort sort);

    @Query("select b from Booking b where b.item.id = ?1 and b.status = 'APPROVED'")
    List<Booking> findBookingsByItem(long itemId);

    @Query("select new ru.practicum.shareit.booking.model.BookingShort(b.id, b.item.id, b.booker.id, b.start, b.end) " +
            "from Booking b " +
            "where b.item.id in ?1 and b.status = 'APPROVED'")
    List<BookingShort> findAllBookingsShortByItemIdIn(List<Long> items, Sort sort);

    @Query("select new ru.practicum.shareit.booking.model.BookingShort(b.id, b.item.id, b.booker.id, b.start, b.end) " +
            "from Booking b " +
            "where b.item.id = ?1 and b.status = 'APPROVED'")
    List<BookingShort> findBookingsShortByItem(long itemId);

    @EntityGraph("booking-graph")
    List<Booking> findByItemIdAndBookerIdAndStatusAndEndBefore(long itemId, long bookerId, BookingStatus status,
                                                               LocalDateTime dateTime);
}