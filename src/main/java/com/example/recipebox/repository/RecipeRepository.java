package com.example.recipebox.repository;

import com.example.recipebox.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    List<Recipe> findByCuisineContainingIgnoreCase(String cuisine);

    List<Recipe> findByTagContainingIgnoreCase(String tag);

    @Query("""
            SELECT DISTINCT i.recipe
            FROM Ingredient i
            WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :ingredient, '%'))
            """)
    List<Recipe> findByIngredient(
            @Param("ingredient") String ingredient
    );
}