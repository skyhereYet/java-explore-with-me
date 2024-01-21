package ru.practicum.explore.likes.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explore.event.dto.EventShortDto;
import ru.practicum.explore.likes.dto.LikeDto;
import ru.practicum.explore.likes.dto.NewLikeDto;
import ru.practicum.explore.user.dto.UserDto;

import java.util.List;

@Service
public interface LikesService {

    LikeDto createOrThrow(NewLikeDto newLikeDto);

    LikeDto deleteOrThrow(int id);

    List<EventShortDto> getEventsLikeSort(PageRequest of);

    List<UserDto> getUsersLikeSort(PageRequest of);
}
