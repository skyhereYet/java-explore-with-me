package ru.practicum.explore.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.requests.dto.ParticipationRequestDto;
import ru.practicum.explore.requests.service.RequestService;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;


@RestController
@RequestMapping("/users/{userId}/requests")
@Validated
@Slf4j
@RequiredArgsConstructor
public class PrivateRequestController {

    @Autowired
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ParticipationRequestDto create(@Positive @PathVariable Integer userId,
                                          @Positive @NotNull @RequestParam(required = false) Integer eventId) {
        log.info("POST request. Request create. User ID: {}, Event ID: {}", userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@Positive @PathVariable Integer userId,
                                                  @Positive @PathVariable Integer requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllRequest(@Positive @PathVariable Integer userId) {
        return requestService.getAllRequest(userId);
    }
}
