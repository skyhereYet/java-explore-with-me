package ru.practicum.explore.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.event.dto.EventFullDto;
import ru.practicum.explore.event.dto.EventsAdminParam;
import ru.practicum.explore.event.dto.UpdateEventAdminRequest;
import ru.practicum.explore.event.model.StateEvent;
import ru.practicum.explore.event.service.EventService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/events")
@Validated
@Slf4j
@AllArgsConstructor
public class AdminEventController {
    @Autowired
    private final EventService eventService;
    private static final String PATTERN_FOR_DATETIME = "yyyy-MM-dd HH:mm:ss";

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getAllByAdminWithParameter(@RequestParam(required = false) List<Integer> users,
                                                        @RequestParam(required = false) List<StateEvent> states,
                                                        @RequestParam(required = false) List<Integer> categories,
                                                        @DateTimeFormat(pattern = PATTERN_FOR_DATETIME)
                                                        @RequestParam(required = false) LocalDateTime rangeStart,
                                                        @DateTimeFormat(pattern = PATTERN_FOR_DATETIME)
                                                        @RequestParam(required = false) LocalDateTime rangeEnd,
                                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(defaultValue = "10") Integer size) {

        EventsAdminParam eventsParam = new EventsAdminParam();
        eventsParam.setUsers(users);
        eventsParam.setStates(states);
        eventsParam.setCategories(categories);
        eventsParam.setRangeStart(rangeStart);
        eventsParam.setRangeEnd(rangeEnd);
        return eventService.getAllByAdmin(eventsParam, PageRequest.of(from / size, size));
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventAdmin(@PathVariable Integer eventId,
                                        @Validated @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {

        return eventService.updateEventAdmin(eventId, updateEventAdminRequest);
    }
}
