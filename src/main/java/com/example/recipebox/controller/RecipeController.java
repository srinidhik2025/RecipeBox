package com.example.recipebox.controller;

import com.example.recipebox.dto.RecipeRequest;
import com.example.recipebox.entity.Recipe;
import com.example.recipebox.entity.User;
import com.example.recipebox.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    public ResponseEntity<Recipe> createRecipe(
            @Valid @RequestBody RecipeRequest request) {

        User user = new User();
        user.setId(request.getUserId());

        Recipe recipe = new Recipe();

        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        recipe.setCuisine(request.getCuisine());
        recipe.setTag(request.getTag());
        recipe.setPrepTime(request.getPrepTime());
        recipe.setSteps(request.getSteps());
        recipe.setFavorite(request.isFavorite());
        recipe.setUser(user);

        Recipe savedRecipe = recipeService.createRecipe(recipe);

        return new ResponseEntity<>(
                savedRecipe,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {

        return ResponseEntity.ok(
                recipeService.getAllRecipes()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                recipeService.getRecipeById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody RecipeRequest request) {

        User user = new User();
        user.setId(request.getUserId());

        Recipe recipe = new Recipe();

        recipe.setName(request.getName());
        recipe.setDescription(request.getDescription());
        recipe.setCuisine(request.getCuisine());
        recipe.setTag(request.getTag());
        recipe.setPrepTime(request.getPrepTime());
        recipe.setSteps(request.getSteps());
        recipe.setFavorite(request.isFavorite());
        recipe.setUser(user);

        return ResponseEntity.ok(
                recipeService.updateRecipe(id, recipe)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(
            @PathVariable Long id) {

        recipeService.deleteRecipe(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/favorite")
    public ResponseEntity<Recipe> toggleFavorite(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                recipeService.toggleFavorite(id)
        );
    }

    // Search by ingredient
    @GetMapping("/search")
    public ResponseEntity<List<Recipe>> searchRecipes(
            @RequestParam(required = false) String ingredient,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) String tag) {

        if (ingredient != null && !ingredient.isBlank()) {
            return ResponseEntity.ok(
                    recipeService.searchByIngredient(ingredient)
            );
        }

        if (cuisine != null && !cuisine.isBlank()) {
            return ResponseEntity.ok(
                    recipeService.searchByCuisine(cuisine)
            );
        }

        if (tag != null && !tag.isBlank()) {
            return ResponseEntity.ok(
                    recipeService.searchByTag(tag)
            );
        }

        return ResponseEntity.badRequest().build();
    }

    // Get recipe count
    @GetMapping("/count")
    public ResponseEntity<Long> getRecipeCount() {

        return ResponseEntity.ok(
                recipeService.getRecipeCount()
        );
    }
}