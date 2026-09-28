package com.example.recipebox.service;

import com.example.recipebox.dto.ShoppingListItem;
import com.example.recipebox.entity.Ingredient;
import com.example.recipebox.entity.MealPlan;
import com.example.recipebox.repository.IngredientRepository;
import com.example.recipebox.repository.MealPlanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShoppingListService {

    private final MealPlanRepository mealPlanRepository;
    private final IngredientRepository ingredientRepository;

    public ShoppingListService(
            MealPlanRepository mealPlanRepository,
            IngredientRepository ingredientRepository) {

        this.mealPlanRepository = mealPlanRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<ShoppingListItem> generateShoppingList(
            Long userId,
            LocalDate startDate,
            LocalDate endDate) {

        // Get all meal plans for the selected user and date range
        List<MealPlan> mealPlans =
                mealPlanRepository.findByUserIdAndMealDateBetween(
                        userId,
                        startDate,
                        endDate
                );

        Map<String, ShoppingListItem> shoppingMap =
                new HashMap<>();

        // Process every meal plan separately
        for (MealPlan mealPlan : mealPlans) {

            if (mealPlan.getRecipe() == null
                    || mealPlan.getRecipe().getId() == null) {
                continue;
            }

            Long recipeId = mealPlan.getRecipe().getId();

            // Get ingredients for this recipe
            List<Ingredient> ingredients =
                    ingredientRepository.findByRecipeIdIn(
                            List.of(recipeId)
                    );

            // Add each ingredient to the shopping list
            for (Ingredient ingredient : ingredients) {

                String key =
                        ingredient.getName().toLowerCase()
                                + "|"
                                + ingredient.getUnit().toLowerCase();

                if (shoppingMap.containsKey(key)) {

                    ShoppingListItem existingItem =
                            shoppingMap.get(key);

                    existingItem.setQuantity(
                            existingItem.getQuantity()
                                    + ingredient.getQuantity()
                    );

                } else {

                    ShoppingListItem item =
                            new ShoppingListItem(
                                    ingredient.getName(),
                                    ingredient.getQuantity(),
                                    ingredient.getUnit()
                            );

                    shoppingMap.put(key, item);
                }
            }
        }

        return new ArrayList<>(
                shoppingMap.values()
        );
    }
}