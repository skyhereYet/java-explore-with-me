package ru.practicum.explore.categories.dto;


import ru.practicum.explore.categories.model.Category;

public class CategoryMapper {
    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }

    public static Category toCategory(NewCategoryDto categoryDto) {
        return new Category(
                0,
                categoryDto.getName()
        );
    }
}