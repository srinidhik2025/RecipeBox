package com.example.recipebox.service;

import com.example.recipebox.entity.Recipe;
import com.example.recipebox.entity.User;
import com.example.recipebox.exception.ResourceNotFoundException;
import com.example.recipebox.repository.RecipeRepository;
import com.example.recipebox.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public RecipeService(RecipeRepository recipeRepository,
                         UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    public Recipe createRecipe(Recipe recipe) {

        User user = userRepository.findById(
                recipe.getUser().getId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "User not found with id: "
                                + recipe.getUser().getId()
                ));

        recipe.setUser(user);

        return recipeRepository.save(recipe);
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Recipe getRecipeById(Long id) {

        return recipeRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Recipe not found with id: " + id
                        ));
    }

    public Recipe updateRecipe(Long id, Recipe recipe) {

        Recipe existingRecipe = getRecipeById(id);

        existingRecipe.setName(recipe.getName());
        existingRecipe.setDescription(recipe.getDescription());
        existingRecipe.setCuisine(recipe.getCuisine());
        existingRecipe.setTag(recipe.getTag());
        existingRecipe.setPrepTime(recipe.getPrepTime());
        existingRecipe.setSteps(recipe.getSteps());
        existingRecipe.setFavorite(recipe.isFavorite());

        if (recipe.getUser() != null
                && recipe.getUser().getId() != null) {

            User user = userRepository.findById(
                    recipe.getUser().getId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "User not found with id: "
                                    + recipe.getUser().getId()
                    ));

            existingRecipe.setUser(user);
        }

        return recipeRepository.save(existingRecipe);
    }

    public void deleteRecipe(Long id) {

        Recipe existingRecipe = getRecipeById(id);

        recipeRepository.delete(existingRecipe);
    }

    public Recipe toggleFavorite(Long id) {

        Recipe recipe = getRecipeById(id);

        recipe.setFavorite(!recipe.isFavorite());

        return recipeRepository.save(recipe);
    }

    // Search by cuisine
    public List<Recipe> searchByCuisine(String cuisine) {

        return recipeRepository.findByCuisineContainingIgnoreCase(
                cuisine
        );
    }

    // Search by tag
    public List<Recipe> searchByTag(String tag) {

        return recipeRepository.findByTagContainingIgnoreCase(
                tag
        );
    }

    // Search by ingredient
    public List<Recipe> searchByIngredient(String ingredient) {

        return recipeRepository.findByIngredient(
                ingredient
        );
    }

    // Get total recipe count
    public long getRecipeCount() {

        return recipeRepository.count();
    }
}