package ru.practicum.explore.compilations.dto;

import ru.practicum.explore.compilations.model.Compilation;
import ru.practicum.explore.event.dto.EventShortDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static CompilationDto toCompilationDto(Compilation compilation, Map<Integer, EventShortDto> eventShortDtoMap) {
        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.isPinned());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setEvents(compilation.getEvents().stream()
                .map(event -> eventShortDtoMap.get(event.getId())).collect(Collectors.toList()));
        return compilationDto;
    }

    public static List<CompilationDto> toCompilationDtoList(List<Compilation> compilations,
                                                    Map<Integer, EventShortDto> eventShortDtoList) {

        List<CompilationDto> compilationDtoList = new ArrayList<>();
        for (Compilation comp : compilations) {
            compilationDtoList.add(toCompilationDto(comp, eventShortDtoList));
        }
        return compilations.stream().map(com -> toCompilationDto(com, eventShortDtoList)).collect(Collectors.toList());
    }

}
