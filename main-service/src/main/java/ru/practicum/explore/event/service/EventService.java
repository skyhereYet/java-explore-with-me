package ru.practicum.explore.event.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.explore.event.dto.*;
import ru.practicum.explore.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore.requests.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto createOrThrow(Integer userId, NewEventDto newEventDto);

    EventFullDto updateOrThrow(Integer userId, Integer eventId, UpdateEventUserRequest updateEvent);

    List<EventShortDto> getAllByUserId(Integer userId, PageRequest of);

    EventFullDto getEventById(Integer userId, Integer eventId);

    List<ParticipationRequestDto> getRequestsByEvent(Integer userId, Integer eventId);

    EventRequestStatusUpdateResult patchRequestsByEventId(Integer userId, Integer eventId, EventRequestStatusUpdateRequest ev);

    List<EventFullDto> getAllByAdmin(EventsAdminParam eventsParam, PageRequest of);

    EventFullDto updateEventAdmin(Integer eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getAllByPublic(EventsPublicParam eventsPublicParam, PageRequest of);

    EventFullDto getById(Integer id, HttpServletRequest request);
}
