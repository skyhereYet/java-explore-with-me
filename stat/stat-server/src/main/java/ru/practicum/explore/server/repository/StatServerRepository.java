package ru.practicum.explore.server.repository;

import dto.StatView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explore.server.model.StatHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatServerRepository extends JpaRepository<StatHit, Integer> {

    @Query("select new dto.StatView(S.app, S.uri, COUNT(distinct S.ip)) " +
            "from StatHit as S " +
            "where (S.timestamp BETWEEN :start AND :end) AND (S.uri IN :uris OR :uris = null) " +
            "GROUP BY S.app, S.uri " +
            "ORDER BY COUNT(DISTINCT S.ip) DESC")
    List<StatView> getAllStatsUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new dto.StatView(S.app, S.uri, count(S.ip)) " +
            "from StatHit as S " +
            "where (S.timestamp BETWEEN :start AND :end) AND (S.uri IN :uris OR :uris = null) " +
            "GROUP BY S.app, S.uri " +
            "ORDER BY COUNT(S.id) DESC")
    List<StatView> getAllStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
