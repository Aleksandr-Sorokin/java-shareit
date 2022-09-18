package ru.practicum.shareit.requests.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.user.model.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class ItemRequestDto {
    private Long id;
    @NotBlank
    private String description;
    private UserDto requestor;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;
}
