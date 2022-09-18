package ru.practicum.shareit.requests.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.requests.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    @Query("select ir from ItemRequest ir where ir.requestor.id = ?1 order by ir.created desc")
    List<ItemRequest> findAllByRequestor(Long requestorId);

    Page<ItemRequest> findAllByRequestorIdIsNot(Long userId, Pageable pageable);
}
