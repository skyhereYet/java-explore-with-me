package ru.practicum.explore.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.compilations.dto.CompilationDto;
import ru.practicum.explore.compilations.dto.NewCompilationDto;
import ru.practicum.explore.compilations.dto.UpdateCompilationRequest;
import ru.practicum.explore.compilations.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequestMapping("/compilations")
@Validated
@Slf4j
@RequiredArgsConstructor
public class PublicCompilationController {

    @Autowired
    private final CompilationService compilationService;

    @GetMapping("")
    public List<CompilationDto> getAll(@RequestParam(defaultValue = "false") boolean pinned,
                                       @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                       @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET request. Get all compilation");
        return compilationService.getAll(pinned, PageRequest.of(from / size, size));
    }

    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable Integer compId) {
        log.info("GET request. Get compilation by ID: " + compId);
        return compilationService.getById(compId);
    }







    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Validated @RequestBody NewCompilationDto newCompilationDto) {
        log.info("POST request. Compilation create: {}", newCompilationDto.toString());
        return compilationService.createCompilation(newCompilationDto);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@Validated @RequestBody UpdateCompilationRequest updateCompilationRequest,
                                           @Positive @PathVariable Integer compId) {
        log.info("PATCH request. Compilation update: {}", updateCompilationRequest.toString());
        return compilationService.updateCompilation(compId, updateCompilationRequest);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@Positive @PathVariable Integer compId) {
        log.info("DELETE request. Compilation delete: {}", compId);
        compilationService.deleteCompilation(compId);
    }

}
