package ru.practicum.shareit.requests.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.item.model.dto.ItemForRequestDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RequestAllWithItemDto {
    private Long id;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YYYY-MM-dd'T'HH:mm:ss")
    private LocalDateTime created;
    private List<ItemForRequestDto> items;
}
