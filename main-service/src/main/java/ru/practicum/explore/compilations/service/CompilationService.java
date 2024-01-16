package ru.practicum.explore.compilations.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.explore.compilations.dto.CompilationDto;
import ru.practicum.explore.compilations.dto.NewCompilationDto;
import ru.practicum.explore.compilations.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto dto);

    CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest dto);

    void deleteCompilation(Integer compId);

    List<CompilationDto> getAll(boolean pinned, PageRequest of);

    CompilationDto getById(Integer compId);
}
