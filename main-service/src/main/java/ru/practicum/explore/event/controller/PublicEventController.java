package ru.practicum.explore.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.event.dto.*;
import ru.practicum.explore.event.model.SortEvent;
import ru.practicum.explore.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@Validated
@Slf4j
@AllArgsConstructor
public class PublicEventController {
    @Autowired
    private final EventService eventService;
    private static final String PATTERN_FOR_DATETIME = "yyyy-MM-dd HH:mm:ss";

    @GetMapping("")
    public List<EventShortDto> getAllByPublic(@RequestParam(required = false) String text,
                                      @RequestParam(required = false) List<Integer> categories,
                                      @RequestParam(required = false) Boolean paid,
                                      @DateTimeFormat(pattern = PATTERN_FOR_DATETIME)
                                      @RequestParam(required = false) LocalDateTime rangeStart,
                                      @DateTimeFormat(pattern = PATTERN_FOR_DATETIME)
                                      @RequestParam(required = false) LocalDateTime rangeEnd,
                                      @RequestParam(defaultValue = "EVENT_DATE") SortEvent sort,
                                      @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                      @Positive @RequestParam(defaultValue = "10") Integer size,
                                      HttpServletRequest request) {
        EventsPublicParam eventsPublicParam = new EventsPublicParam();
        eventsPublicParam.setIp(request.getRemoteAddr());
        eventsPublicParam.setText(text);
        eventsPublicParam.setPaid(paid);
        eventsPublicParam.setUri(request.getRequestURI());
        eventsPublicParam.setSort(sort);
        eventsPublicParam.setCategories(categories);
        eventsPublicParam.setRangeStart(rangeStart);
        eventsPublicParam.setRangeEnd(rangeEnd);
        eventsPublicParam.setOnlyAvailable(onlyAvailable);
        return eventService.getAllByPublic(eventsPublicParam, PageRequest.of(from / size, size));
    }

    @GetMapping("/{id}")
    public EventFullDto getById(@PathVariable Integer id,  HttpServletRequest request) {
        return eventService.getById(id, request);
    }
}
