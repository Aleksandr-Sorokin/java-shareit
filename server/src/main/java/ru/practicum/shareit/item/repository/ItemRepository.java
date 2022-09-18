package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(value = "select i from Item i " +
            "where i.available = true and lower(i.description) like lower(concat('%', ?1, '%')) " +
            "or lower(i.name) like lower(concat('%', ?1, '%'))")
    List<Item> search(String text, Pageable pageable);

    Page<Item> findAllByOwner_Id(long ownerId, Pageable pageable);

    @Query(value = "select i from Item i  where i.request.id = ?1 order by i.request.created desc")
    List<Item> findAllItemByRequest_Id(Long requestId);
}
