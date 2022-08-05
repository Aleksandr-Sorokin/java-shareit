package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * // TODO .
 */
@Data
public class Item {
    private long id;
    @NotBlank(message = "Необходимо название для вещи")
    private String name;
    @NotBlank(message = "Необходимо описание для вещи")
    @Size(max = 300, message = "Длина описания не должна превышать 300 символов")
    private String description;
    @NotNull
    private Boolean available; //статус о том, доступна или нет вещь для аренды
    @NotBlank
    private User owner; //владелец вещи
    private String request; //если вещь была создана по запросу другого пользователя, то ссылка на запрос
}