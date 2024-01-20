package ru.practicum.explore.likes.dto;

import ru.practicum.explore.likes.model.Like;

public class LikeMapper {
    public static LikeDto toLikeDto(Like like) {
        return new LikeDto(like.getId(),
                like.isLike(),
                like.getLikeOwner().getId(),
                like.getUser().getId(),
                like.getEvent().getId());
    }
}
