package ru.practicum.shareit.item.model.dto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    private String text;
    private ItemDto item;
    private String authorName;
    private LocalDateTime created;
}
