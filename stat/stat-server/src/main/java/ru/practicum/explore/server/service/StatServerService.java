package ru.practicum.explore.server.service;

import dto.HitDto;
import dto.StatView;

import java.time.LocalDateTime;
import java.util.List;

public interface StatServerService {

    HitDto createHit(HitDto hitDto);

    List<StatView> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
