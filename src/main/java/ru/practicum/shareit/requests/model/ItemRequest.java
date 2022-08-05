package ru.practicum.shareit.requests.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;
import javax.persistence.*;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

/**
 * // TODO .
 */

@Data
public class ItemRequest {
    private long id;
    private String description;
    private User requestor;
    private LocalDateTime created;
}
