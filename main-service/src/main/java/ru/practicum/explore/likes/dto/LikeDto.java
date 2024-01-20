package ru.practicum.explore.likes.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LikeDto {
    private int id;
    private boolean like;
    private int likeOwnerId;
    private int userId;
    private int eventId;
}
