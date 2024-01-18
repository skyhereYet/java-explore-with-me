package ru.practicum.explore.requests.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.event.model.StateEvent;
import ru.practicum.explore.exception.ConflictRuleException;
import ru.practicum.explore.exception.RequestExistException;
import ru.practicum.explore.exception.UserExistException;
import ru.practicum.explore.requests.dto.ParticipationRequestDto;
import ru.practicum.explore.requests.dto.RequestMapper;
import ru.practicum.explore.requests.model.StateRequest;
import ru.practicum.explore.requests.repository.RequestRepository;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.event.repository.EventRepository;
import ru.practicum.explore.exception.EventExistException;
import ru.practicum.explore.requests.model.Request;
import ru.practicum.explore.user.model.User;
import ru.practicum.explore.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@AllArgsConstructor
@Slf4j
@Transactional
public class RequestServiceDao implements RequestService {

    @Autowired
    private final RequestRepository requestRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Integer userId, Integer eventId) {
        //check user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserExistException("User not exist in the repository, ID: "  + userId));
        log.info("Check user successfully. User ID: " + userId);

        //check event
        Event eventDao = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventExistException("User not exist in the repository, ID: " + eventId));
        log.info("Check event successfully. Event: " + eventDao.toString());

        //check rules request
        checkRuleRequest(userId, eventId, eventDao);
        log.info("Check rules request successfully.");

        //create request
        Request request = new Request();
        //check moderation
        if (!eventDao.getRequestModeration() || eventDao.getParticipantLimit() == 0) {
            request.setStatus(StateRequest.CONFIRMED);
        } else {
            request.setStatus(StateRequest.PENDING);
        }
        request.setCreated(LocalDateTime.now());
        request.setEvent(eventDao);
        request.setRequester(user);
        log.info("Create request successfully. Request: " + request.toString());
        return RequestMapper.mapToDto(requestRepository.save(request));
    }



    @Override
    @Transactional
    public List<ParticipationRequestDto> getAllRequest(Integer userId) {
        //check user
        userRepository.findById(userId)
                .orElseThrow(() -> new UserExistException("User not exist in the repository, ID: "  + userId));
        log.info("Check user successfully. User ID: " + userId);
        return requestRepository.findAllByRequesterId(userId)
                                            .stream()
                                            .map(request ->
                                                    RequestMapper.toParticipationRequestDto(request))
                                            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Integer userId, Integer requestId) {
        //check user
        userRepository.findById(userId)
                .orElseThrow(() -> new UserExistException("User not exist in the repository, ID: "  + userId));
        log.info("Check user successfully. User ID: " + userId);
        //check request
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RequestExistException("Request not exist in the repository, ID: "  + requestId));
        log.info("Check request successfully. Request ID: " + requestId);
        request.setStatus(StateRequest.CANCELED);
        return RequestMapper.mapToDto(request);
    }

    private void checkRuleRequest(Integer userId, Integer eventId, Event eventDao) {
        //check for repeated request (error 409)
        List<Request> listRequest = requestRepository.findAllByRequesterIdAndEventId(userId, eventId);
        if (listRequest.size() > 0) {
            throw new ConflictRuleException("Repeated request. Do not worry.");
        }

        //check requester and initiator (error 409)
        if (eventDao.getInitiator().getId() == userId) {
            throw new ConflictRuleException("Initiator cannot add a request.");
        }

        //check state event (error 409)
        if (!eventDao.getState().equals(StateEvent.PUBLISHED)) {
            throw new ConflictRuleException("Event not published. Please wait and try again.");
        }

        //check participant limit (error 409)
        if (eventDao.getParticipantLimit() > 0) {
            int confirmedRequests = requestRepository.findAllByStatusAndEvent(
                    StateRequest.CONFIRMED,
                    eventDao).size();
            if (confirmedRequests >= eventDao.getParticipantLimit()) {
                throw new ConflictRuleException("Limit of participation requests reached.");
            }
        }
    }
}
