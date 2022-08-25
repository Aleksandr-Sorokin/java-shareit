package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "where b.booker.id = ?1 and b.status = ?2")
    List<Booking> findAllByBookerAndStatus(Long bookerId, Status status, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "where b.booker.id = ?1 and b.end < ?2")
    List<Booking> findAllByBookerPast(Long bookerId, LocalDateTime time, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "where b.booker.id = ?1 and b.start > ?2")
    List<Booking> findAllByBookerFuture(Long bookerId, LocalDateTime time, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "where b.booker.id = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByBookerCurrent(Long bookerId, LocalDateTime time, Pageable pageable);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "where b.item.owner.id = ?1 and b.status = ?2")
    List<Booking> findAllByOwnerAndStatus(Long ownerId, Status status, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "where b.item.owner.id = ?1 and b.end < ?2")
    List<Booking> findAllByOwnerPast(Long ownerId, LocalDateTime time, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "where b.item.owner.id = ?1 and b.start > ?2")
    List<Booking> findAllByOwnerFuture(Long ownerId, LocalDateTime time, Pageable pageable);

    @Query(value = "select b from Booking b " +
            "where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2")
    List<Booking> findAllByOwnerCurrent(Long ownerId, LocalDateTime time, Pageable pageable);

    Booking findFirstByItem_IdAndBooker_Id(Long itemId, Long bookerId);

    Booking findFirstByItem_IdAndAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime time);

    Booking findFirstByItem_IdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime time);
}
