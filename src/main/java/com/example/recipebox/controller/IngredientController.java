package com.example.recipebox.controller;

import com.example.recipebox.dto.IngredientRequest;
import com.example.recipebox.entity.Ingredient;
import com.example.recipebox.entity.Recipe;
import com.example.recipebox.service.IngredientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    @PostMapping
    public ResponseEntity<Ingredient> createIngredient(
            @Valid @RequestBody IngredientRequest request) {

        Recipe recipe = new Recipe();
        recipe.setId(request.getRecipeId());

        Ingredient ingredient = new Ingredient();

        ingredient.setName(request.getName());
        ingredient.setQuantity(request.getQuantity());
        ingredient.setUnit(request.getUnit());
        ingredient.setRecipe(recipe);

        Ingredient savedIngredient =
                ingredientService.createIngredient(ingredient);

        return new ResponseEntity<>(
                savedIngredient,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<Ingredient>> getAllIngredients() {
        return ResponseEntity.ok(
                ingredientService.getAllIngredients()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingredient> getIngredientById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ingredientService.getIngredientById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ingredient> updateIngredient(
            @PathVariable Long id,
            @Valid @RequestBody IngredientRequest request) {

        Recipe recipe = new Recipe();
        recipe.setId(request.getRecipeId());

        Ingredient ingredient = new Ingredient();

        ingredient.setName(request.getName());
        ingredient.setQuantity(request.getQuantity());
        ingredient.setUnit(request.getUnit());
        ingredient.setRecipe(recipe);

        return ResponseEntity.ok(
                ingredientService.updateIngredient(id, ingredient)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(
            @PathVariable Long id) {

        ingredientService.deleteIngredient(id);

        return ResponseEntity.noContent().build();
    }
}