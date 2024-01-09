package ru.practicum.explore.server.service;



import dto.Hit;
import dto.StatView;
import ru.practicum.explore.server.model.StatHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatServerService {

    StatHit createHit(Hit hit);

    List<StatView> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
