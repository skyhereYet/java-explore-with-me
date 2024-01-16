package ru.practicum.explore.compilations.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class NewCompilationDto {
    @Size(min = 1, max = 50)
    private String title;
    private List<Integer> events;
    private boolean pinned;
}
