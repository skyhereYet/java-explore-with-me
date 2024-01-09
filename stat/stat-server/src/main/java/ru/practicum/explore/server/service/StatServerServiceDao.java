package ru.practicum.explore.server.service;

import dto.Hit;
import dto.StatView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.server.exception.DateTimeValidateException;
import ru.practicum.explore.server.mapper.StatHitMapper;
import ru.practicum.explore.server.model.StatHit;
import ru.practicum.explore.server.repository.StatServerRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatServerServiceDao implements StatServerService {

    private final StatServerRepository statServerRepository;

    @Override
    @Transactional(readOnly = false)
    public StatHit createHit(Hit hit) {
        return statServerRepository.save(StatHitMapper.toStatHit(hit));
    }

    @Override
    public List<StatView> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new DateTimeValidateException("Start and end time exception: start is after end or equal");
        }
        if (unique) {
            return statServerRepository.getAllStatsUnique(start, end, uris);
        } else {
            return statServerRepository.getAllStats(start, end, uris);
        }
    }
}
