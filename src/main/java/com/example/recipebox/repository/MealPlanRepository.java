package com.example.recipebox.repository;

import com.example.recipebox.entity.MealPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealPlanRepository extends JpaRepository<MealPlan, Long> {

    List<MealPlan> findByUserIdAndMealDateBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
}