package ru.practicum.explore.user.dto;

import ru.practicum.explore.user.model.User;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        int likes = user.getLikes().stream()
                                            .filter(like -> {
                                                return like.isLike();
                                            })
                                            .collect(Collectors.toList()).size();
        int dislikes = user.getLikes().stream()
                                            .filter(like -> {
                                                return !like.isLike();
                                            })
                                            .collect(Collectors.toList()).size();
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                likes,
                dislikes);
    }

    public static User toUser(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail(),
                new ArrayList<>()
        );
    }
}