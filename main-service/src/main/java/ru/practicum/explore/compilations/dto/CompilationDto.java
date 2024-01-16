package ru.practicum.explore.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.explore.event.dto.EventShortDto;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private int id;
    @Size(min = 1, max = 50)
    private String title;
    private List<EventShortDto> events;
    private boolean pinned;
}
