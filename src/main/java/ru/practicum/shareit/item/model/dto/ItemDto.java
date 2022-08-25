package ru.practicum.shareit.item.model.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ItemDto {
    private Long id;
    @NotBlank(message = "Необходимо название для вещи")
    @Size(max = 100, message = "Длина название не должна превышать 100 символов")
    private String name;
    @NotBlank(message = "Необходимо описание для вещи")
    @Size(max = 250, message = "Длина описания не должна превышать 250 символов")
    private String description;
    @NotNull
    private Boolean available; //статус о том, доступна или нет вещь для аренды
    private UserDto owner; //владелец вещи
    private Long requestId; //если вещь была создана по запросу другого пользователя, то ссылка на запрос
    private List<CommentDto> comments;
}
