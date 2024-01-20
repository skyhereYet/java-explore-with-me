package ru.practicum.explore.likes.dto;


import lombok.*;
import ru.practicum.explore.util.Create;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NewLikeDto {
    private boolean isLike;
    @NotNull(groups = {Create.class})
    @Positive(groups = {Create.class})
    private int likeOwnerId;
    @NotNull(groups = {Create.class})
    @Positive(groups = {Create.class})
    private int userId;
    @NotNull(groups = {Create.class})
    @Positive(groups = {Create.class})
    private int eventId;
}
