package ru.practicum.explore.server.controller;

import dto.Hit;
import dto.StatView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.server.model.StatHit;
import ru.practicum.explore.server.service.StatServerService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatServerController {

    private final StatServerService statServerService;
    private static final String PATTERN_FOR_DATETIME = "yyyy-MM-dd HH:mm:ss";

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatHit createHit(@RequestBody Hit hit) {
        log.info("POST request. Create a hit: " + hit.toString());
        return statServerService.createHit(hit);
    }

    @GetMapping(value = "/stats")
    public List<StatView> getAllStats(@RequestParam @DateTimeFormat(pattern = PATTERN_FOR_DATETIME) LocalDateTime start,
                                      @RequestParam @DateTimeFormat(pattern = PATTERN_FOR_DATETIME) LocalDateTime end,
                                      @RequestParam(required = false) List<String> uris,
                                      @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("GET request. Parameters: \n\tstart: {}\n\tend: {}\n\turis: {}\n\tunique: {}",
                start, end, uris, unique);
        return statServerService.getAllStats(start, end, uris, unique);
    }
}
