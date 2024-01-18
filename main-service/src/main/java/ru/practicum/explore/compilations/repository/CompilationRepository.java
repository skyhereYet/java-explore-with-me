package ru.practicum.explore.compilations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.explore.compilations.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Integer>, QuerydslPredicateExecutor<Compilation> {
}
