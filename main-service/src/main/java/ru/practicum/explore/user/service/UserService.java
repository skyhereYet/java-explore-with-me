package ru.practicum.explore.user.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.explore.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createOrThrow(UserDto userDto);

    List<UserDto> getAllUsers(Integer[] ids, PageRequest pageRequest);

    UserDto deleteOrThrow(int id);
}
