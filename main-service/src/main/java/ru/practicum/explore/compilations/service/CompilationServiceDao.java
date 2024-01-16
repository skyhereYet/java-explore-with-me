package ru.practicum.explore.compilations.service;

import com.querydsl.core.BooleanBuilder;
import dto.StatView;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.client.service.StatClientService;
import ru.practicum.explore.compilations.dto.CompilationDto;
import ru.practicum.explore.compilations.dto.CompilationMapper;
import ru.practicum.explore.compilations.dto.NewCompilationDto;
import ru.practicum.explore.compilations.dto.UpdateCompilationRequest;
import ru.practicum.explore.compilations.model.Compilation;
import ru.practicum.explore.compilations.model.QCompilation;
import ru.practicum.explore.compilations.repository.CompilationRepository;
import ru.practicum.explore.event.dto.EventMapper;
import ru.practicum.explore.event.dto.EventShortDto;
import ru.practicum.explore.event.model.Event;
import ru.practicum.explore.event.repository.EventRepository;
import ru.practicum.explore.exception.CompilationExistException;
import ru.practicum.explore.requests.model.StateRequest;
import ru.practicum.explore.requests.model.ViewRequest;
import ru.practicum.explore.requests.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Primary
@AllArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CompilationServiceDao implements CompilationService {

    private final LocalDateTime dateTimeStart = LocalDateTime.of(0001, 01,01,00,01);
    private final LocalDateTime dateTimeEnd = LocalDateTime.of(3000, 01,01,00,01);
    @Autowired
    private final CompilationRepository compilationRepository;
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
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation();
        compilation.setTitle(newCompilationDto.getTitle());
        compilation.setPinned(newCompilationDto.isPinned());
        if (newCompilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllById(newCompilationDto.getEvents()));
        } else {
            compilation.setEvents(new ArrayList<>());
        }
        log.info("Create compilation succesfully.");
        compilationRepository.save(compilation);
        log.info("Save compilation succesfully.");
        Map<Integer, EventShortDto> eventShortDtoMap = getEventShortDtoMap(compilation);
        return CompilationMapper.toCompilationDto(compilation, eventShortDtoMap);
    }

    @Override
    @Transactional(readOnly = false)
    public CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationExistException("Compilation not found ID: " + compId));
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(updateCompilationRequest.getEvents());
            compilation.setEvents(events);
        }
        log.info("Check compilation succesfully.");

        compilationRepository.save(compilation);
        log.info("Save compilation succesfully.");
        Map<Integer, EventShortDto> eventShortDtoMap = getEventShortDtoMap(compilation);
        return CompilationMapper.toCompilationDto(compilation, eventShortDtoMap);
    }

    @Override
    public void deleteCompilation(Integer compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationExistException("Compilation not found ID: " + compId));
        log.info("Check compilation succesfully.");
        compilationRepository.delete(compilation);
    }

    @Override
    public List<CompilationDto> getAll(boolean pinned, PageRequest pageRequest) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QCompilation qCompilation = QCompilation.compilation;
        Set<Event> events = new HashSet<>();
        booleanBuilder.and(qCompilation.pinned.eq(pinned));
        List<Compilation> compilations = compilationRepository.findAll(booleanBuilder, pageRequest).toList();
        for (Compilation comp : compilations) {
            events.addAll(comp.getEvents());
        }
        List<String> uris = events.stream().map(event1 -> "/events/" + event1.getId()).collect(Collectors.toList());
        List<ViewRequest> viewRequest = requestRepository.findViewRequest(events.stream().collect(Collectors.toList()),
                StateRequest.CONFIRMED);
        List<StatView> viewStatList = statClientService.getAllStats(dateTimeStart, dateTimeEnd, uris, true);
        Map<Integer, EventShortDto> eventShortDtoMap = eventMapper.toEventShortDtoMap(events.stream()
                .collect(Collectors.toList()), viewStatList, viewRequest);
        return CompilationMapper.toCompilationDtoList(compilations, eventShortDtoMap);
    }

    @Override
    public CompilationDto getById(Integer compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationExistException("Compilation not found ID: " + compId));
        Map<Integer, EventShortDto> eventShortDtoMap = getEventShortDtoMap(compilation);
        return CompilationMapper.toCompilationDto(compilation, eventShortDtoMap);
    }

    public Map<Integer, EventShortDto> getEventShortDtoMap(Compilation compilation) {
        List<String> uris = compilation.getEvents().stream()
                .map(e -> "/events/" + e.getId()).collect(Collectors.toList());
        List<ViewRequest> viewRequestList = requestRepository.findViewRequest(compilation.getEvents(),
                StateRequest.CONFIRMED);
        List<StatView> viewStatList = statClientService.getAllStats(dateTimeStart, dateTimeEnd, uris, true);
        return eventMapper.toEventShortDtoMap(compilation.getEvents(), viewStatList, viewRequestList);
    }
}
