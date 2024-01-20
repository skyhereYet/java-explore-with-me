package ru.practicum.explore.likes.service;

import dto.StatView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.client.service.StatClientService;
import ru.practicum.explore.event.dto.EventMapper;
import ru.practicum.explore.event.dto.EventShortDto;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.event.model.StateEvent;
import ru.practicum.explore.event.repository.EventRepository;
import ru.practicum.explore.exception.ConflictRuleException;
import ru.practicum.explore.exception.EventExistException;
import ru.practicum.explore.exception.LikeExistException;
import ru.practicum.explore.exception.UserExistException;
import ru.practicum.explore.likes.dto.LikeDto;
import ru.practicum.explore.likes.dto.LikeMapper;
import ru.practicum.explore.likes.dto.NewLikeDto;
import ru.practicum.explore.likes.model.Like;
import ru.practicum.explore.likes.repository.LikesRepository;
import ru.practicum.explore.requests.model.StateRequest;
import ru.practicum.explore.requests.model.ViewRequest;
import ru.practicum.explore.requests.repository.RequestRepository;
import ru.practicum.explore.user.dto.UserDto;
import ru.practicum.explore.user.dto.UserMapper;
import ru.practicum.explore.user.model.User;
import ru.practicum.explore.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LikesServiceDao implements LikesService {

    private final LocalDateTime dateTimeStart = LocalDateTime.of(0001, 01,01,00,01);
    private final LocalDateTime dateTimeEnd = LocalDateTime.of(3000, 01,01,00,01);
    @Value("${app.name}")
    private String appName;
    @Autowired
    private final LikesRepository likesRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final RequestRepository requestRepository;
    @Autowired
    private final StatClientService statClientService;
    @Autowired
    private final EventMapper eventMapper;

    @Override
    @Transactional(readOnly = false)
    public LikeDto createOrThrow(NewLikeDto newLikeDto) {
        log.info("Create like: {}", newLikeDto.toString());
        //check exist like
        if (likesRepository.findByLikeOwnerIdAndEventId(newLikeDto.getLikeOwnerId(), newLikeDto.getEventId()) != null) {
            throw new ConflictRuleException("Like from user ID: " + newLikeDto.getLikeOwnerId()
                    + " to event ID: " + newLikeDto.getEventId() + " exist.");
        }
        log.info("Check like successfully.");

        //check user
        User user = userRepository.findById(newLikeDto.getUserId())
                .orElseThrow(() -> new UserExistException("User not exist in the repository, ID: "
                        + newLikeDto.getLikeOwnerId()));
        log.info("Check user successfully: {}", user.toString());

        //check owner
        User owner = userRepository.findById(newLikeDto.getLikeOwnerId())
                .orElseThrow(() -> new UserExistException("User not exist in the repository, ID: "
                        + newLikeDto.getLikeOwnerId()));
        log.info("Check owner successfully: {}", owner.toString());

        //check event
        Event event = eventRepository.findById(newLikeDto.getEventId())
                .orElseThrow(() -> new EventExistException("User not exist in the repository, ID: "
                        + newLikeDto.getEventId()));
        if (!event.getState().equals(StateEvent.PUBLISHED)) {
            throw new ConflictRuleException("Event not published. Please contact administrator.");
        }
        log.info("Check event successfully: {}", event.toString());

        //check user & owner
        if (user.getId() == owner.getId()) {
            throw new ConflictRuleException("You can't like yourself");
        }
        log.info("Check user & owner successfully.");

        //create like
        Like like = new Like(0, true, owner, user, event);
        if (!newLikeDto.isLike()) {
            like.setLike(false);
        }
        return LikeMapper.toLikeDto(likesRepository.save(like));
    }


    @Override
    @Transactional(readOnly = false)
    public LikeDto deleteOrThrow(int id) {
        //check exist like
        Like likeDao = likesRepository.findById(id)
                .orElseThrow(() -> new LikeExistException("Like not exist in the repository, ID: " + id));
        log.info("Check like successfully: {}", likeDao.toString());

        //delete like
        likesRepository.deleteById(id);
        return LikeMapper.toLikeDto(likeDao);
    }

    @Override
    public List<EventShortDto> getEventsLikeSort(PageRequest pageRequest) {
        List<Event> events = eventRepository.findAllByState(StateEvent.PUBLISHED, pageRequest);
        List<String> uris = events.stream().map(e -> "/events/" + e.getId()).collect(Collectors.toList());
        List<ViewRequest> viewRequest = requestRepository.findViewRequest(events, StateRequest.CONFIRMED);

        //load statistic
        List<StatView> statViewList = statClientService.getAllStats(dateTimeStart, dateTimeEnd, uris, true);
        //mapping lists
        List<EventShortDto> eventShortDtoList = eventMapper.toEventShortDtoList(events, statViewList, viewRequest);
        log.info("Create list event successfully: {}", eventShortDtoList.toString());
        Collections.sort(eventShortDtoList, EventShortDto.likesComparator);
        return eventShortDtoList;
    }

    @Override
    public List<UserDto> getUsersLikeSort(PageRequest pageRequest) {
        Page<User> users = userRepository.findAll(pageRequest);
        List<UserDto> userDtoList = users.stream().map(u -> {
                                                                return UserMapper.toUserDto(u);
                                                            }).collect(Collectors.toList());
        Collections.sort(userDtoList, UserDto.likesComparator);
        return userDtoList;
    }
}
