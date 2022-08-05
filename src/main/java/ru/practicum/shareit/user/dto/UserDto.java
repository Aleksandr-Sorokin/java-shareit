package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    private long id;
    @NotBlank(message = "Имя не должено быть пустым")
    private String name;
    @NotBlank
    @Email(message = "Проверьте корректность email")
    private String email;
}
