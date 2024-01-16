package ru.practicum.explore.categories.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.categories.dto.NewCategoryDto;
import ru.practicum.explore.categories.repository.CategoriesRepository;
import ru.practicum.explore.exception.CategoryExistException;
import ru.practicum.explore.categories.dto.CategoryDto;
import ru.practicum.explore.categories.dto.CategoryMapper;
import ru.practicum.explore.categories.model.Category;
import ru.practicum.explore.exception.ConflictRuleException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@AllArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CategoriesServiceDao implements CategoriesService {

    @Autowired
    private final CategoriesRepository categoriesRepository;

    @Override
    @Transactional(readOnly = false)
    public CategoryDto createOrThrow(NewCategoryDto categoryDto) {
        log.info("Create Category: {}", categoryDto.toString());
        Category categoryByName = categoriesRepository.findByName(categoryDto.getName());
        if (categoryByName != null) {
            throw new ConflictRuleException("exist name!");
        }
        return CategoryMapper.toCategoryDto(
                categoriesRepository.save(CategoryMapper.toCategory(categoryDto))
        );
    }

    @Override
    @Transactional(readOnly = false)
    public CategoryDto deleteOrThrow(int id) {
        CategoryDto categoryDto = CategoryMapper.toCategoryDto(
                categoriesRepository.findById(id)
                        .orElseThrow(() -> new CategoryExistException("Category not exist in the repository, ID: "  + id)));
        categoriesRepository.deleteById(id);
        return categoryDto;
    }

    @Override
    @Transactional(readOnly = false)
    public CategoryDto updateOrThrow(int catId, NewCategoryDto categoryDto) {
        log.info("Update category. id: {}. Category: {}", catId, categoryDto);
        categoriesRepository.findById(catId)
                .orElseThrow(() -> new CategoryExistException("Category not exist in the repository, ID: "  + catId));
        Category categoryByName = categoriesRepository.findByName(categoryDto.getName());
        if (categoryByName != null && categoryByName.getId() != catId) {
            throw new ConflictRuleException("exist name!");
        }
        return CategoryMapper.toCategoryDto(
                    categoriesRepository.save(new Category(catId, categoryDto.getName())));
    }

    @Override
    public List<CategoryDto> getAllCategories(PageRequest pageRequest) {
        return categoriesRepository.findAll(pageRequest).stream()
                .map(category -> {
                    return CategoryMapper.toCategoryDto(category);
                })
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(int catId) {
        return CategoryMapper.toCategoryDto(categoriesRepository.findById(catId)
                .orElseThrow(() -> new CategoryExistException("Category with ID: " + catId + " not found")));
    }
}
