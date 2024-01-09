package ru.practicum.explore.server.mapper;

import dto.Hit;
import ru.practicum.explore.server.model.StatHit;

import java.time.LocalDateTime;

public class StatHitMapper {

    public static StatHit toStatHit(Hit hit) {
        StatHit statHit = new StatHit();
        statHit.setIp(hit.getIp());
        statHit.setApp(hit.getApp());
        statHit.setUri(hit.getUri());
        if (hit.getTimestamp() == null) {
            statHit.setTimestamp(LocalDateTime.now());
        } else {
            statHit.setTimestamp(hit.getTimestamp());
        }
        return statHit;
    }
}
