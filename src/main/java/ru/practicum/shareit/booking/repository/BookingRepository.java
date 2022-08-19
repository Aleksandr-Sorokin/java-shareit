package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    @Query(value = "select b from Booking b " +
            "where b.booker.id = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findAllByBookerAndStatus(Long bookerId, Status status);

    @Query(value = "select b from Booking b " +
            "where b.booker.id = ?1 and b.end < ?2 order by b.start desc")
    List<Booking> findAllByBookerPast(Long bookerId, LocalDateTime time);

    @Query(value = "select b from Booking b " +
            "where b.booker.id = ?1 and b.start > ?2 order by b.start desc")
    List<Booking> findAllByBookerFuture(Long bookerId, LocalDateTime time);

    @Query(value = "select b from Booking b " +
            "where b.booker.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start desc")
    List<Booking> findAllByBookerCurrent(Long bookerId, LocalDateTime time);

    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId);

    @Query(value = "select b from Booking b " +
            "where b.item.owner.id = ?1 and b.status = ?2 order by b.start desc")
    List<Booking> findAllByOwnerAndStatus(Long ownerId, Status status);

    @Query(value = "select b from Booking b " +
            "where b.item.owner.id = ?1 and b.end < ?2 order by b.start desc")
    List<Booking> findAllByOwnerPast(Long ownerId, LocalDateTime time);

    @Query(value = "select b from Booking b " +
            "where b.item.owner.id = ?1 and b.start > ?2 order by b.start desc")
    List<Booking> findAllByOwnerFuture(Long ownerId, LocalDateTime time);

    @Query(value = "select b from Booking b " +
            "where b.item.owner.id = ?1 and b.start < ?2 and b.end > ?2 order by b.start desc")
    List<Booking> findAllByOwnerCurrent(Long ownerId, LocalDateTime time);

    Booking findFirstByItem_IdAndBooker_Id(Long itemId, Long bookerId);

    Booking findFirstByItem_IdAndAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime time);

    Booking findFirstByItem_IdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime time);
}
