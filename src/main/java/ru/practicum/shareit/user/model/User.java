package ru.practicum.shareit.user.model;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * // TODO .
 */

@Data
public class User {
    private long id;
    @NotBlank(message = "Имя не должено быть пустым")
    private String name;
    @NotBlank
    @Email(message = "Проверьте корректность email")
    private String email;
}
