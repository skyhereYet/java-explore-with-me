package ru.practicum.explore.requests.service;

import ru.practicum.explore.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Integer userId, Integer eventId);

    List<ParticipationRequestDto> getAllRequest(Integer userId);

    ParticipationRequestDto cancelRequest(Integer userId, Integer requestId);
}
