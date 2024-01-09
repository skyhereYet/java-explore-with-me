package ru.practikum.explore;

import dto.Hit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.explore.client.StatClientApp;

import java.time.LocalDateTime;

@Component
@Slf4j
public class TestClient {
    public static void testClient() {
        StatClientApp statClientApp = new StatClientApp("http://localhost:9090");
        Hit hitDto = new Hit(0,
                "main-server",
                "http://localhost/from-main-server",
                "192.168.0.1",
                LocalDateTime.now());
        Hit hit = statClientApp.createHit(hitDto);
        log.info("Hit original: " +
                "\n\tid: {}" +
                "\n\tapp: {}" +
                "\n\turi: {}" +
                "\n\tip: {}" +
                "\n\ttimestamp: {}", hitDto.getId(), hitDto.getApp(), hitDto.getUri(), hitDto.getIp(), hitDto.getTimestamp());
        log.info("Hit from client: " +
                "\n\tid: {}" +
                "\n\tapp: {}" +
                "\n\turi: {}" +
                "\n\tip: {}" +
                "\n\ttimestamp: {}", hit.getId(), hit.getApp(), hit.getUri(), hit.getIp(), hit.getTimestamp());
    }
}
