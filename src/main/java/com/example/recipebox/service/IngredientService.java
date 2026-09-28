package com.example.recipebox.service;

import com.example.recipebox.entity.Ingredient;
import com.example.recipebox.entity.Recipe;
import com.example.recipebox.exception.ResourceNotFoundException;
import com.example.recipebox.repository.IngredientRepository;
import com.example.recipebox.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;

    public IngredientService(IngredientRepository ingredientRepository,
                             RecipeRepository recipeRepository) {
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
    }

    public Ingredient createIngredient(Ingredient ingredient) {

        Recipe recipe = recipeRepository.findById(
                ingredient.getRecipe().getId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Recipe not found with id: "
                                + ingredient.getRecipe().getId()
                ));

        ingredient.setRecipe(recipe);

        return ingredientRepository.save(ingredient);
    }

    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    public Ingredient getIngredientById(Long id) {

        return ingredientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ingredient not found with id: " + id
                        ));
    }

    public Ingredient updateIngredient(Long id, Ingredient ingredient) {

        Ingredient existingIngredient = getIngredientById(id);

        existingIngredient.setName(ingredient.getName());
        existingIngredient.setQuantity(ingredient.getQuantity());
        existingIngredient.setUnit(ingredient.getUnit());

        if (ingredient.getRecipe() != null
                && ingredient.getRecipe().getId() != null) {

            Recipe recipe = recipeRepository.findById(
                    ingredient.getRecipe().getId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Recipe not found with id: "
                                    + ingredient.getRecipe().getId()
                    ));

            existingIngredient.setRecipe(recipe);
        }

        return ingredientRepository.save(existingIngredient);
    }

    public void deleteIngredient(Long id) {

        Ingredient existingIngredient = getIngredientById(id);

        ingredientRepository.delete(existingIngredient);
    }
}