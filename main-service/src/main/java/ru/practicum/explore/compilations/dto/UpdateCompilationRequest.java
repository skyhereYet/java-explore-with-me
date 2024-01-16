package ru.practicum.explore.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {
    @Size(min = 1, max = 50)
    private String title;
    private List<Integer> events;
    private Boolean pinned;
}
