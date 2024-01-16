package ru.practicum.explore.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.categories.dto.CategoryDto;
import ru.practicum.explore.categories.service.CategoriesService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequestMapping(path = "/categories")
@Validated
@Slf4j
@RequiredArgsConstructor
public class PublicCategoriesController {

    @Autowired
    private final CategoriesService categoriesService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategories(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("\nGET request. Get all categories");
        return categoriesService.getAllCategories(PageRequest.of(from / size, size));
    }

    @GetMapping(value = "/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable int catId) {
        log.info("\nGet request. Get category by ID: {}", catId);
        return categoriesService.getCategoryById(catId);
    }
}
