package ru.practicum.explore.categories.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.categories.model.Category;

@Repository
public interface CategoriesRepository extends JpaRepository<Category, Integer> {
    Category findByName(String name);
}
