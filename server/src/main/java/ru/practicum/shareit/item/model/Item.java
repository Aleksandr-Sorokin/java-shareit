package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @Column(name = "description", nullable = false, length = 250)
    private String description;
    private Boolean available; //статус о том, доступна или нет вещь для аренды
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner; //владелец вещи
    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request; //если вещь была создана по запросу другого пользователя, то ссылка на запрос
    @Transient
    private List<Comment> comments;
}