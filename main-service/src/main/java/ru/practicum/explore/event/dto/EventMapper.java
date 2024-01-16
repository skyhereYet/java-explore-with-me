package ru.practicum.explore.event.dto;

import dto.StatView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.explore.categories.dto.CategoryDto;
import ru.practicum.explore.categories.model.Category;
import ru.practicum.explore.compilations.dto.CompilationDto;
import ru.practicum.explore.compilations.model.Compilation;
import ru.practicum.explore.event.model.Location;
import ru.practicum.explore.event.model.StateEvent;
import ru.practicum.explore.exception.ConflictRuleException;
import ru.practicum.explore.exception.RequestInvalidException;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.requests.model.ViewRequest;
import ru.practicum.explore.user.dto.UserShortDto;
import ru.practicum.explore.user.model.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EventMapper {
    @Value("${app.name}")
    private String appName;

    @Autowired
    public EventMapper(@Value("${app.name}") String appName) {
        this.appName = appName;
    }

    public Event toEvent(NewEventDto newEventDto, Category category, User user) {
        Event event = new Event();
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setCreatedOn(LocalDateTime.now());
        event.setLocation(newEventDto.getLocation());
        event.setState(StateEvent.PENDING);
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setInitiator(user);
        event.setTitle(newEventDto.getTitle());
        if (newEventDto.getPaid() != null) {
            event.setPaid(newEventDto.getPaid());
        } else {
            event.setPaid(false);
        }
        if (newEventDto.getRequestModeration() != null) {
            event.setRequestModeration(newEventDto.getRequestModeration());
        } else {
            event.setRequestModeration(true);
        }
        return event;
    }

    public EventFullDto toFullEventDto(Event event, long views, int confirmedRequests) {
        EventFullDto fullDto = new EventFullDto();
        fullDto.setId(event.getId());
        fullDto.setDescription(event.getDescription());
        fullDto.setEventDate(event.getEventDate());
        fullDto.setCreatedOn(event.getCreatedOn());
        fullDto.setLocation(toLocationDto(event.getLocation()));
        fullDto.setState(event.getState());
        fullDto.setParticipantLimit(event.getParticipantLimit());
        fullDto.setRequestModeration(event.getRequestModeration());
        fullDto.setAnnotation(event.getAnnotation());
        fullDto.setPaid(event.isPaid());
        fullDto.setCategory(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()));
        fullDto.setInitiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        fullDto.setTitle(event.getTitle());
        fullDto.setViews(views);
        fullDto.setConfirmedRequests(confirmedRequests);
        return fullDto;
    }

    public LocationDto toLocationDto(Location location) {
        LocationDto locationDto = new LocationDto();
        locationDto.setLat(location.getLat());
        locationDto.setLon(location.getLon());
        return locationDto;
    }

    public Event toEventFromUpdateEvent(Event eventDao, UpdateEventUserRequest updateEvent) {
        if (eventDao.getState() == StateEvent.PUBLISHED) {
            throw new ConflictRuleException("Only pending or canceled events can be changed");
        }
        if (updateEvent.getPaid() != null) {
            eventDao.setPaid(updateEvent.getPaid());
        }
        if (updateEvent.getRequestModeration() != null) {
            eventDao.setRequestModeration(updateEvent.getRequestModeration());
        }
        if (updateEvent.getAnnotation() != null) {
            eventDao.setAnnotation(updateEvent.getAnnotation());
        }

        if (updateEvent.getDescription() != null) {
            eventDao.setDescription(updateEvent.getDescription());
        }

        if (updateEvent.getTitle() != null) {
            eventDao.setTitle(updateEvent.getTitle());
        }
        if (updateEvent.getEventDate() != null) {
            if (updateEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new RequestInvalidException("Field: eventDate. Error: должно содержать дату, " +
                        "которая еще не наступила. Value: " + updateEvent.getEventDate().toString());
            }
            eventDao.setEventDate(updateEvent.getEventDate());
        }
        if (updateEvent.getParticipantLimit() != null) {
            eventDao.setParticipantLimit(updateEvent.getParticipantLimit());
        }
        if (updateEvent.getStateAction() != null) {
            if (updateEvent.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                eventDao.setState(StateEvent.PENDING);
            } else {
                eventDao.setState(StateEvent.CANCELED);
            }
        }
        return eventDao;
    }

    public List<EventShortDto> toEventShortDtoList(List<Event> eventList, List<StatView> statViewList,
                                                          List<ViewRequest> viewRequestList) {
        return eventList.stream()
                .map(event -> toEventShortDto(event, statViewList, viewRequestList))
                .collect(Collectors.toList());
    }

    private EventShortDto toEventShortDto(Event event, List<StatView> statViewList, List<ViewRequest> viewRequestList) {
        long views = 0;
        int confirmedRequests = 0;
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()));
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setInitiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setPaid(event.isPaid());
        eventShortDto.setId(event.getId());
        String uri = "/events/" + event.getId();
        for (ViewRequest viewRequest : viewRequestList) {
            if (viewRequest.getEvent().equals(event.getId())) {
                confirmedRequests = viewRequest.getHit();
                break;
            }
        }
        eventShortDto.setConfirmedRequests(confirmedRequests);
        for (StatView statView : statViewList) {
            if (appName.equals(statView.getApp()) && statView.getUri().equals(uri)) {
                views = statView.getHits();
                break;
            }
        }
        eventShortDto.setViews(views);
        return eventShortDto;
    }

    public List<EventFullDto> toAdminEventList(List<Event> eventList, List<StatView> viewStatList,
                                                      List<ViewRequest> viewRequestList) {
        return eventList.stream()
                .map(event -> toEventFullDto(event, viewStatList, viewRequestList))
                .collect(Collectors.toList());
    }

    public EventFullDto toEventFullDto(Event event, List<StatView> viewStatList, List<ViewRequest> viewRequestList) {
        long views = 0;
        int confirmedRequests = 0;
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setId(event.getId());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setLocation(toLocationDto(event.getLocation()));
        String uri = "/events/" + event.getId();
        for (StatView statView : viewStatList) {
            if (appName.equals(statView.getApp()) && statView.getUri().equals(uri)) {
                views = statView.getHits();
                break;
            }
        }
        eventFullDto.setViews(views);

        for (ViewRequest viewRequest : viewRequestList) {
            if (viewRequest.getEvent().equals(event.getId())) {
                confirmedRequests = viewRequest.getHit();
                break;
            }
        }
        eventFullDto.setConfirmedRequests(confirmedRequests);
        eventFullDto.setState(event.getState());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        eventFullDto.setRequestModeration(event.getRequestModeration());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setPaid(event.isPaid());
        eventFullDto.setCategory(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()));
        eventFullDto.setInitiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        eventFullDto.setTitle(event.getTitle());
        return eventFullDto;
    }

    public Map<Integer, EventShortDto> toEventShortDtoMap(List<Event> events,
                                                          List<StatView> viewStatList,
                                                          List<ViewRequest> viewRequestList) {
            Map<Integer, EventShortDto> eventShortDtoMap = new HashMap<>();
            List<EventShortDto> eventShortDtos = events.stream()
                    .map(event -> toEventShortDto(event, viewStatList, viewRequestList)).collect(Collectors.toList());
            for (EventShortDto eventShortDto : eventShortDtos) {
                eventShortDtoMap.put(eventShortDto.getId(), eventShortDto);
            }
            return eventShortDtoMap;
        }








    public Map<Integer, EventShortDto> toPublicEventMap(List<Event> events, List<StatView> viewStatList,
                                                        List<ViewRequest> viewRequsts) {
        Map<Integer, EventShortDto> eventShortDto = new HashMap<>();
        List<EventShortDto> eventShortDtos = events.stream()
                .map(event -> toMapperShort(event, viewStatList, viewRequsts)).collect(Collectors.toList());
        for (EventShortDto ev : eventShortDtos) {
            eventShortDto.put(ev.getId(), ev);
        }
        return eventShortDto;
    }

    private EventShortDto toMapperShort(Event event, List<StatView> viewStats, List<ViewRequest> viewRequsts) {
        long views = 0;
        int confirmedRequests = 0;
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setCategory(new CategoryDto(event.getCategory().getId(), event.getCategory().getName()));
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setInitiator(new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()));
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setPaid(event.isPaid());
        eventShortDto.setId(event.getId());
        String uri = "/events/" + event.getId();
        for (StatView vs : viewStats) {
            if (appName.equals(vs.getApp()) && vs.getUri().equals(uri)) {
                views = vs.getHits();
                break;
            }
        }
        eventShortDto.setViews(views);

        for (ViewRequest vr : viewRequsts) {
            if (vr.getEvent().equals(event.getId())) {
                confirmedRequests = vr.getHit();
                break;
            }
        }
        eventShortDto.setConfirmedRequests(confirmedRequests);
        return eventShortDto;
    }

    public CompilationDto mapperCompilationDto(Compilation com, Map<Integer, EventShortDto> eventShortDtos) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(com.getId());
        compilationDto.setPinned(com.isPinned());
        compilationDto.setTitle(com.getTitle());
        compilationDto.setEvents(com.getEvents().stream()
                .map(event -> eventShortDtos.get(event.getId())).collect(Collectors.toList()));
        return compilationDto;
    }
}
