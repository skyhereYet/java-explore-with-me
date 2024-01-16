package ru.practicum.explore.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.categories.dto.CategoryDto;
import ru.practicum.explore.categories.dto.NewCategoryDto;
import ru.practicum.explore.categories.service.CategoriesService;
import ru.practicum.explore.util.Create;
import ru.practicum.explore.util.Update;


@RestController
@RequestMapping(path = "/admin/categories")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AdminCategoriesController {

    @Autowired
    private final CategoriesService categoriesService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CategoryDto create(@Validated({Create.class}) @RequestBody NewCategoryDto categoryDto) {
        log.info("POST request. Category create: {}", categoryDto.toString());
        return categoriesService.createOrThrow(categoryDto);
    }

    @PatchMapping(value = "/{catId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CategoryDto update(@PathVariable int catId,
                              @Validated({Update.class}) @RequestBody NewCategoryDto categoryDto)  {
        log.info("PATCH request. Category change id: {}. Category: {}", catId, categoryDto);
        return categoriesService.updateOrThrow(catId, categoryDto);
    }


    @DeleteMapping(value = "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CategoryDto deleteUserById(@PathVariable int catId) {
        log.info("DELETE request. Delete category by ID: {}", catId);
        return categoriesService.deleteOrThrow(catId);
    }
}
