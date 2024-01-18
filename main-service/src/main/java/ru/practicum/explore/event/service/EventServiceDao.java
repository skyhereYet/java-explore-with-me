package ru.practicum.explore.event.service;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import dto.HitDto;
import dto.StatView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.categories.model.Category;
import ru.practicum.explore.categories.repository.CategoriesRepository;
import ru.practicum.explore.client.service.StatClientService;
import ru.practicum.explore.event.dto.*;
import ru.practicum.explore.event.model.*;
import ru.practicum.explore.event.repository.EventRepository;
import ru.practicum.explore.event.repository.LocationRepository;
import ru.practicum.explore.exception.*;
import ru.practicum.explore.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.explore.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.explore.requests.dto.ParticipationRequestDto;
import ru.practicum.explore.requests.dto.RequestMapper;
import ru.practicum.explore.requests.model.QRequest;
import ru.practicum.explore.requests.model.Request;
import ru.practicum.explore.requests.model.StateRequest;
import ru.practicum.explore.requests.model.ViewRequest;
import ru.practicum.explore.requests.repository.RequestRepository;
import ru.practicum.explore.user.repository.UserRepository;
import ru.practicum.explore.user.model.User;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
@Transactional
public class EventServiceDao implements EventService {

    private final LocalDateTime dateTimeStart = LocalDateTime.of(0001, 01,01,00,01);
    private final LocalDateTime dateTimeEnd = LocalDateTime.of(3000, 01,01,00,01);

    @Value("${app.name}")
    private String appName;
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final CategoriesRepository categoriesRepository;
    @Autowired
    private final LocationRepository locationRepository;
    @Autowired
    private final StatClientService statClientService;
    @Autowired
    private final RequestRepository requestRepository;
    @Autowired
    private final EventMapper eventMapper;

    @Override
    public EventFullDto createOrThrow(Integer userId, NewEventDto newEventDto) {
        //check user
        User user = checkUserExist(userId);
        log.info("Check user successfully. User: " + user.toString());
        //check date and time
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new RequestInvalidException("Field: eventDate. Error: должно содержать дату, " +
                    "которая еще не наступила. Value: " + newEventDto.getEventDate().toString());
        }
        log.info("Check date and time successfully. Event date: " + newEventDto.getEventDate().toString());
        //check category
        Category category = categoriesRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new CategoryExistException("Category not exist in the repository, ID: "
                        + newEventDto.getCategory()));
        log.info("Check category successfully. Category: " + category.toString());
        //create Event
        Event eventDto = eventMapper.toEvent(newEventDto, category, user);
        return eventMapper.toFullEventDto(eventRepository.save(eventDto), 0, 0);
    }

    @Override
    public EventFullDto updateOrThrow(Integer userId, Integer eventId, UpdateEventUserRequest updateEventDto) {
        //check user
        checkUserExist(userId);
        log.info("Check user successfully. User ID: " + userId);

        //check event
        Event eventDao = checkEventExist(eventId);
        log.info("Check event successfully. Event: " + eventDao.toString());

        //check location
        Location location = checkAndGetLocation(updateEventDto.getLocation(), eventDao);
        log.info("Check location successfully. Location: " + location.toString());

        //check category
        if (updateEventDto.getCategory() != null) {
            Category category = categoriesRepository.findById(updateEventDto.getCategory())
                    .orElseThrow(() -> new CategoryExistException("Category not exist in the repository, ID: "
                            + updateEventDto.getCategory()));
            eventDao.setCategory(category);
        }
        log.info("Check category successfully. Category: " + eventDao.getCategory().toString());

        //update eventDao
        Event event = eventMapper.toEventFromUpdateEvent(eventDao, updateEventDto);

        //load statistic
        long views = getStatisticView(eventId);
        log.info("Load statistic successfully. Views: " + views);

        //load requests
        int confirmRequests = requestRepository.findAllByStatusAndEvent(StateRequest.CONFIRMED, event).size();
        return eventMapper.toFullEventDto(eventRepository.save(eventDao), views, confirmRequests);
    }

    @Override
    public List<EventShortDto> getAllByUserId(Integer userId, PageRequest of) {
        //check user
        checkUserExist(userId);
        log.info("Check user successfully. User ID: " + userId);

        //load events
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QEvent event = QEvent.event;
        booleanBuilder.and(event.initiator.id.eq(userId));
        List<Event> eventList = eventRepository.findAll(booleanBuilder, of).toList();
        log.info("Load events successfully. Events: " + eventList.toString());

        //load viewRequest
        List<ViewRequest> viewRequestList = requestRepository.findViewRequest(eventList, StateRequest.CONFIRMED);
        log.info("Load viewRequest successfully. View Requests: " + viewRequestList);

        //load statistic from StatClientService
        List<StatView> viewStatList = getStatViewsFromStatClientService(dateTimeStart, dateTimeEnd, eventList);

        //mapping collections
        List<EventShortDto> eventShortDtoList = eventMapper.toEventShortDtoList(eventList, viewStatList, viewRequestList);
        Collections.sort(eventShortDtoList, EventShortDto.dateComparator);
        return eventShortDtoList;
    }

    @Override
    public EventFullDto getEventById(Integer userId, Integer eventId) {
        //check user
        checkUserExist(userId);
        log.info("Check user successfully. User ID: " + userId);

        //check event
        Event eventDao = checkEventExist(eventId);
        log.info("Check event successfully. Event: " + eventDao.toString());

        //load statistic
        long views = getStatisticView(eventId);
        log.info("Load statistic successfully. Views: " + views);

        //load requests
        int confirmRequests = requestRepository.findAllByStatusAndEvent(StateRequest.CONFIRMED, eventDao).size();
        return eventMapper.toFullEventDto(eventDao, views, confirmRequests);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEvent(Integer userId, Integer eventId) {
        //check user
        checkUserExist(userId);
        log.info("Check user successfully. User ID: " + userId);

        //check event
        Event eventDao = checkEventExist(eventId);
        log.info("Check event successfully. Event ID: " + eventDao.toString());

        //load request list
        List<Request> requestList = requestRepository.findAllByStatusAndEvent(StateRequest.PENDING, eventDao);
        return RequestMapper.toListParticipationRequestDto(requestList);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult patchRequestsByEventId(Integer userId, Integer eventId,
                                                                 EventRequestStatusUpdateRequest ev) {
        //check user
        checkUserExist(userId);
        log.info("Check user successfully. User ID: " + userId);

        //check event
        Event eventDao = checkEventExist(eventId);
        log.info("Check event successfully. Event ID: " + eventDao.toString());

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        EventRequestStatusUpdateResult toReturn = new EventRequestStatusUpdateResult();
        Request request;
        List<Integer> requestIdList = ev.getRequestIds();
        boolean needReject = false;

        //check limit participant
        int limitParticipant = eventDao.getParticipantLimit();
        int countRequest = requestRepository.findAllByStatusAndEvent(StateRequest.CONFIRMED, eventDao).size();
        if (limitParticipant > 0 && limitParticipant <= countRequest && ev.getStatus() == StateRequest.CONFIRMED) {
            List<Request> requestList = requestRepository.findAllByStatusAndEvent(StateRequest.PENDING, eventDao);
            for (Request requestCheck : requestList) {
                requestCheck.setStatus(StateRequest.REJECTED);
                requestRepository.save(requestCheck);
            }
            throw new ConflictRuleException("Request must have status PENDING");
        }

        //set reject requests
        if (ev.getStatus() == StateRequest.REJECTED) {
            for (int requestId : requestIdList) {
                request = requestRepository.findById(requestId)
                        .orElseThrow(() -> new RequestExistException("Request not exist in the repository, ID: "  + requestId));
                if (request.getStatus() != StateRequest.PENDING) {
                    throw new ConflictRuleException("Request must have status PENDING");
                }
                request.setStatus(StateRequest.REJECTED);
                requestRepository.save(request);
                rejectedRequests.add(RequestMapper.toParticipationRequestDto(request));
            }
            toReturn.setConfirmedRequests(confirmedRequests);
            toReturn.setRejectedRequests(rejectedRequests);
            return toReturn;

        /*set confirm requests
        (если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется)*/
        } else {
            //limit participant zero
            if (limitParticipant == 0) {
                for (int requestId : requestIdList) {
                    request = requestRepository.findById(requestId)
                            .orElseThrow(() -> new RequestExistException("Request not exist in the repository, ID: "
                                                                        + requestId));
                    if (request.getStatus() != StateRequest.PENDING) {
                        throw new ConflictRuleException("Request must have status PENDING");
                    }
                    request.setStatus(StateRequest.CONFIRMED);
                    requestRepository.save(request);
                    confirmedRequests.add(RequestMapper.toParticipationRequestDto(request));
                }
                toReturn.setConfirmedRequests(confirmedRequests);
                toReturn.setRejectedRequests(rejectedRequests);
                return toReturn;

            //limit participant not zero
            } else {
                for (int requestId : requestIdList) {
                    request = requestRepository.findById(requestId)
                            .orElseThrow(() -> new RequestExistException("Request not exist in the repository, ID: "
                                    + requestId));

                    if (request.getStatus() != StateRequest.PENDING) {
                        throw new ConflictRuleException("Request must have status PENDING");
                    }
                    if (countRequest < limitParticipant) {
                        request.setStatus(StateRequest.CONFIRMED);
                        requestRepository.save(request);
                        countRequest++;
                        confirmedRequests.add(RequestMapper.toParticipationRequestDto(request));
                    } else {
                        needReject = true;
                        request.setStatus(StateRequest.REJECTED);
                        requestRepository.save(request);
                        rejectedRequests.add(RequestMapper.toParticipationRequestDto(request));
                    }
                }
                if (needReject) {
                    List<Request> requsts = requestRepository.findAllByStatusAndEvent(StateRequest.PENDING, eventDao);
                    for (Request req : requsts) {
                        req.setStatus(StateRequest.REJECTED);
                        requestRepository.save(req);
                    }
                }
                toReturn.setConfirmedRequests(confirmedRequests);
                toReturn.setRejectedRequests(rejectedRequests);
                return toReturn;
            }
        }
    }

    @Override
    public List<EventFullDto> getAllByAdmin(EventsAdminParam eventsParam, PageRequest pageRequest) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QEvent qEvent = QEvent.event;
        LocalDateTime rangeStart;
        LocalDateTime rangeEnd;

        //check event parameter and fill the gaps BooleanBuilder
        if (eventsParam.getRangeStart() != null && eventsParam.getRangeEnd() != null) {
            if (eventsParam.getRangeStart().isAfter(eventsParam.getRangeEnd())) {
                throw new BadRequestException("Error: start date after end date.");
            }
        }
        if (eventsParam.getUsers() != null) {
            booleanBuilder.and(qEvent.initiator.id.in(eventsParam.getUsers()));
        }
        if (eventsParam.getStates() != null) {
            booleanBuilder.and(qEvent.state.in(eventsParam.getStates()));
        }
        if (eventsParam.getCategories() != null) {
            booleanBuilder.and(qEvent.category.id.in(eventsParam.getCategories()));
        }
        if (eventsParam.getRangeStart() != null) {
            rangeStart = eventsParam.getRangeStart();
        } else {
            rangeStart = LocalDateTime.now();
        }
        if (eventsParam.getRangeEnd() != null) {
            rangeEnd = eventsParam.getRangeEnd();
        } else {
            rangeEnd = dateTimeEnd;
        }
        log.info("Check event parameter and fill the gaps BooleanBuilder successfully. BooleanBuilder: "
                + booleanBuilder.toString());

        //load events
        booleanBuilder.and(qEvent.eventDate.after(rangeStart)).and(qEvent.eventDate.before(rangeEnd));
        List<Event> eventList = eventRepository.findAll(booleanBuilder, pageRequest).toList();
        log.info("Load events successfully. Event list: " + eventList.toString());

        //load viewRequest
        List<ViewRequest> viewRequestList = requestRepository.findViewRequest(eventList, StateRequest.CONFIRMED);
        log.info("Load viewRequest successfully. View Requests: " + viewRequestList);

        //load statistic from StatClientService
        List<StatView> viewStatList = getStatViewsFromStatClientService(rangeStart, rangeEnd, eventList);

        //mapping collections
        return eventMapper.toAdminEventList(eventList, viewStatList, viewRequestList);
    }

    @Override
    public EventFullDto updateEventAdmin(Integer eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        //check event
        Event eventDao = checkEventExist(eventId);
        log.info("Check event successfully. Event ID: " + eventDao.toString());

        //check category
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoriesRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new CategoryExistException("Category not exist in the repository, ID: "
                            + updateEventAdminRequest.getCategory()));
            eventDao.setCategory(category);
            log.info("Check category successfully. Category: " + category.toString());
        }

        //check location
        Location location = checkAndGetLocation(updateEventAdminRequest.getLocation(), eventDao);
        log.info("Check location successfully. Location: " + location.toString());

        //check fields
        if (updateEventAdminRequest.getEventDate() != null) {
            eventDao.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (LocalDateTime.now().plus(Duration.ofHours(1)).isAfter(eventDao.getEventDate())) {
            throw new RequestInvalidException("Field: eventDate. Error: должно содержать дату," +
                    " которая еще не наступила. Value: " + eventDao.getEventDate().toString());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            eventDao.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getDescription() != null) {
            eventDao.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            eventDao.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            eventDao.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            eventDao.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            eventDao.setTitle(updateEventAdminRequest.getTitle());
        }
        if (updateEventAdminRequest.getStateAction() == StateAction.PUBLISH_EVENT && eventDao.getState() != StateEvent.PENDING) {
            throw new ConflictRuleException("Cannot publish the event because it's not in the right state: " +
                    eventDao.getState());
        } else if (updateEventAdminRequest.getStateAction() == StateAction.PUBLISH_EVENT) {
            eventDao.setState(StateEvent.PUBLISHED);
            eventDao.setPublishedOn(LocalDateTime.now());
        }
        if (updateEventAdminRequest.getStateAction() == StateAction.REJECT_EVENT && eventDao.getState() == StateEvent.PUBLISHED) {
            throw new ConflictRuleException("Wrong state: " +
                    eventDao.getState());
        } else if (updateEventAdminRequest.getStateAction() == StateAction.REJECT_EVENT) {
            eventDao.setState(StateEvent.CANCELED);
        }

        //load statistic
        long views = getStatisticView(eventId);
        log.info("Load statistic successfully. Views: " + views);

        Event eventDaoNew = eventRepository.save(eventDao);
        //load requests
        int confirmRequests = requestRepository.findAllByStatusAndEvent(StateRequest.CONFIRMED, eventDao).size();
        return eventMapper.toFullEventDto(eventDaoNew, views, confirmRequests);
    }

    @Override
    public List<EventShortDto> getAllByPublic(EventsPublicParam eventsPublicParam, PageRequest pageRequest) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QEvent qEvent = QEvent.event;
        QRequest request = QRequest.request;
        LocalDateTime rangeStart;
        LocalDateTime rangeEnd;

        //check event parameter and fill the gaps BooleanBuilder
        if (eventsPublicParam.getRangeStart() != null && eventsPublicParam.getRangeEnd() != null) {
            if (eventsPublicParam.getRangeStart().isAfter(eventsPublicParam.getRangeEnd())) {
                throw new BadRequestException("Error: start date after end date.");
            }
        }
        if (eventsPublicParam.getText() != null) {
            booleanBuilder.and(qEvent.annotation.containsIgnoreCase(eventsPublicParam.getText())
                    .or(qEvent.description.containsIgnoreCase(eventsPublicParam.getText())));
        }
        if (eventsPublicParam.getCategories() != null) {
            List<Integer> categories = eventsPublicParam.getCategories();
            booleanBuilder.and(qEvent.category.id.in(categories));
        }
        if (eventsPublicParam.getPaid() != null) {
            booleanBuilder.and(qEvent.paid.eq(eventsPublicParam.getPaid()));
        }
        if (eventsPublicParam.getRangeStart() != null) {
            rangeStart = eventsPublicParam.getRangeStart();
        } else {
            rangeStart = LocalDateTime.now();
        }
        if (eventsPublicParam.getRangeEnd() != null) {
            rangeEnd = eventsPublicParam.getRangeEnd();
        } else {
            rangeEnd = dateTimeEnd;
        }
        booleanBuilder.and(qEvent.eventDate.after(rangeStart)).and(qEvent.eventDate.before(rangeEnd));

        if (eventsPublicParam.getOnlyAvailable() != null) {
            booleanBuilder.and(qEvent.participantLimit.goe(JPAExpressions.select(request.id.countDistinct())
                    .from(request).where(request.status.eq(StateRequest.CONFIRMED)
                            .and(request.event.eq(qEvent)))).or(qEvent.participantLimit.eq(0)));
        }
        List<Event> events = eventRepository.findAll(booleanBuilder.getValue(), pageRequest).getContent();

        List<String> uris = events.stream().map(e -> "/events/" + e.getId()).collect(Collectors.toList());
        List<ViewRequest> viewReqest = requestRepository.findViewRequest(events, StateRequest.CONFIRMED);

        //load statistic
        List<StatView> statViewList = statClientService.getAllStats(dateTimeStart, dateTimeEnd, uris, true);
        //mapping lists
        List<EventShortDto> eventShortDtoList = eventMapper.toEventShortDtoList(events, statViewList, viewReqest);
        if (eventsPublicParam.getSort() != null) {
            SortEvent sort = eventsPublicParam.getSort();
            if (sort.equals(SortEvent.EVENT_DATE)) {
                Collections.sort(eventShortDtoList, EventShortDto.dateComparator);
            } else {
                Collections.sort(eventShortDtoList, EventShortDto.viewsComparator);
            }
        }

        //make statistic
        HitDto hitDto = new HitDto();
        hitDto.setUri(eventsPublicParam.getUri());
        hitDto.setIp(eventsPublicParam.getIp());
        hitDto.setTimestamp(LocalDateTime.now());
        hitDto.setApp(appName);
        statClientService.createHit(hitDto);
        return eventShortDtoList;
    }

    @Override
    public EventFullDto getById(Integer id, HttpServletRequest request) {
        //check event
        Event eventDao = checkEventExist(id);
        log.info("Check event successfully. Event: " + eventDao.toString());

        //check state
        if (eventDao.getState() != StateEvent.PUBLISHED) {
            throw new StateEventException("Status not published");
        }

        //make statistic
        HitDto hitDto = new HitDto();
        hitDto.setUri(getStringUri(eventDao.getId()));
        hitDto.setIp(request.getRemoteAddr());
        hitDto.setTimestamp(LocalDateTime.now());
        hitDto.setApp(appName);
        statClientService.createHit(hitDto);

        //load statistic
        long views = getStatisticView(eventDao.getId());
        log.info("Load statistic successfully. Views: " + views);

        //load requests
        int confirmRequests = requestRepository.findAllByStatusAndEvent(StateRequest.CONFIRMED, eventDao).size();
        Event eventDaoNew = eventRepository.save(eventDao);
        return eventMapper.toFullEventDto(eventDaoNew, views, confirmRequests);
    }


    private String getStringUri(int eventId) {
        return "/events/" + eventId;
    }

    private long getStatisticView(int eventId) {
        String uris = getStringUri(eventId);
        List<StatView> viewStatList = statClientService.getAllStats(dateTimeStart,
                dateTimeEnd, List.of(uris), true);
        if (viewStatList.size() == 0) {
            return 0;
        } else {
            return viewStatList.stream().mapToLong(value -> value.getHits()).sum();
        }
    }

    private User checkUserExist(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserExistException("User not exist in the repository, ID: "  + userId));
    }

    private Event checkEventExist(Integer eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventExistException("User not exist in the repository, ID: " + eventId));
    }

    private List<StatView> getStatViewsFromStatClientService(LocalDateTime start, LocalDateTime end, List<Event> eventList) {
        //create uris
        List<String> uris = eventList.stream()
                .map(uri -> "/events/" + uri.getId())
                .collect(Collectors.toList());
        log.info("Make uris successfully. Uris: " + uris);
        //load statistic
        List<StatView> viewStatList = statClientService.getAllStats(start, end, uris, true);
        log.info("Load statistic successfully. ViewStatList: " + viewStatList);
        return viewStatList;
    }

    private Location checkAndGetLocation(LocationDto locationDto, Event eventDao) {
        Location location = new Location();
        if (locationDto != null) {
            location = locationRepository.getLocationsByLatAndLon(locationDto.getLat(), locationDto.getLon());
            if (location != null) {
                eventDao.setLocation(location);
            } else {
                if (eventDao.getLocation().getLat() != locationDto.getLat() ||
                        eventDao.getLocation().getLon() != locationDto.getLon()) {
                    location = new Location();
                    location.setLat(locationDto.getLat());
                    location.setLon(locationDto.getLon());
                    locationRepository.save(location);
                    eventDao.setLocation(location);
                }
            }
        }
        return location;
    }
}
