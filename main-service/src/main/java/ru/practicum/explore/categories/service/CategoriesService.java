package ru.practicum.explore.categories.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.explore.categories.dto.NewCategoryDto;
import ru.practicum.explore.categories.dto.CategoryDto;

import java.util.List;


public interface CategoriesService {
    CategoryDto createOrThrow(NewCategoryDto categoryDto);

    CategoryDto deleteOrThrow(int id);

    CategoryDto updateOrThrow(int catId, NewCategoryDto categoryDto);

    List<CategoryDto> getAllCategories(PageRequest pageRequest);

    CategoryDto getCategoryById(int catId);
}
