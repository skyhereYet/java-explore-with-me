package ru.practicum.explore.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.compilations.dto.CompilationDto;
import ru.practicum.explore.compilations.dto.NewCompilationDto;
import ru.practicum.explore.compilations.dto.UpdateCompilationRequest;
import ru.practicum.explore.compilations.service.CompilationService;

import javax.validation.constraints.Positive;


@RestController
@RequestMapping("/admin/compilations")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AdminCompilationController {

    @Autowired
    private final CompilationService compilationService;

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
