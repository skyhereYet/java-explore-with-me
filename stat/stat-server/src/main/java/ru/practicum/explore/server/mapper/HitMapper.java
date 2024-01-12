package ru.practicum.explore.server.mapper;

import dto.HitDto;
import ru.practicum.explore.server.model.Hit;

import java.time.LocalDateTime;

public class HitMapper {

    public static Hit toHit(HitDto hitDto) {
        Hit hit = new Hit();
        hit.setIp(hitDto.getIp());
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        if (hitDto.getTimestamp() == null) {
            hit.setTimestamp(LocalDateTime.now());
        } else {
            hit.setTimestamp(hitDto.getTimestamp());
        }
        return hit;
    }

    public static HitDto toHitDto(Hit hit) {
        HitDto hitDto = new HitDto();
        hitDto.setIp(hit.getIp());
        hitDto.setApp(hit.getApp());
        hitDto.setUri(hit.getUri());
        if (hit.getTimestamp() == null) {
            hitDto.setTimestamp(LocalDateTime.now());
        } else {
            hitDto.setTimestamp(hit.getTimestamp());
        }
        return hitDto;
    }
}
