package ru.practicum.explore.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.event.dto.EventFullDto;
import ru.practicum.explore.event.dto.EventShortDto;
import ru.practicum.explore.event.dto.NewEventDto;
import ru.practicum.explore.event.dto.UpdateEventUserRequest;
import ru.practicum.explore.event.service.EventService;
import ru.practicum.explore.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore.requests.dto.ParticipationRequestDto;
import ru.practicum.explore.util.Create;
import ru.practicum.explore.util.Update;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Validated
@Slf4j
@AllArgsConstructor
public class PrivateEventController {

    @Autowired
    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto createEvent(@Positive @PathVariable Integer userId,
                                    @Validated({Create.class}) @RequestBody NewEventDto newEventDto) {
        log.info("\nPOST request. User ID: {}. Event create: {}", userId, newEventDto.toString());
        return eventService.createOrThrow(userId, newEventDto);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(value = HttpStatus.OK)
    public EventFullDto updateEvent(@Positive @PathVariable Integer userId,
                                    @Positive @PathVariable Integer eventId,
                                    @Validated({Update.class}) @RequestBody UpdateEventUserRequest updateEvent) {
        log.info("\nPOST request. \n\tUser ID: {}. \n\tEvent ID: {}. \n\tEvent create: {}",
                userId, eventId, updateEvent.toString());
        return eventService.updateOrThrow(userId, eventId, updateEvent);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getAll(@Positive @PathVariable Integer userId,
                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                      @Positive @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getAllByUserId(userId, PageRequest.of(from / size, size));
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventById(@Positive @PathVariable Integer userId,
                                     @Positive @PathVariable Integer eventId) {
        return eventService.getEventById(userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByEventId(@Positive @PathVariable Integer userId,
                                                            @Positive @PathVariable Integer eventId) {
        return eventService.getRequestsByEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult patchRequestsByEventId(@PathVariable Integer userId,
                                                               @PathVariable Integer eventId,
                                                               @RequestBody EventRequestStatusUpdateRequest ev) {
        return eventService.patchRequestsByEventId(userId, eventId, ev);
    }
}
