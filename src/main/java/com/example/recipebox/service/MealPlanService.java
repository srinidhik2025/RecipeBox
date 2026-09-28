package com.example.recipebox.service;

import com.example.recipebox.entity.MealPlan;
import com.example.recipebox.entity.Recipe;
import com.example.recipebox.entity.User;
import com.example.recipebox.exception.ResourceNotFoundException;
import com.example.recipebox.repository.MealPlanRepository;
import com.example.recipebox.repository.RecipeRepository;
import com.example.recipebox.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public MealPlanService(MealPlanRepository mealPlanRepository,
                           RecipeRepository recipeRepository,
                           UserRepository userRepository) {
        this.mealPlanRepository = mealPlanRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    public MealPlan createMealPlan(MealPlan mealPlan) {

        User user = userRepository.findById(
                mealPlan.getUser().getId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "User not found with id: "
                                + mealPlan.getUser().getId()
                ));

        Recipe recipe = recipeRepository.findById(
                mealPlan.getRecipe().getId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Recipe not found with id: "
                                + mealPlan.getRecipe().getId()
                ));

        mealPlan.setUser(user);
        mealPlan.setRecipe(recipe);

        return mealPlanRepository.save(mealPlan);
    }

    public List<MealPlan> getAllMealPlans() {
        return mealPlanRepository.findAll();
    }

    public MealPlan getMealPlanById(Long id) {

        return mealPlanRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Meal plan not found with id: " + id
                        ));
    }

    public MealPlan updateMealPlan(Long id, MealPlan mealPlan) {

        MealPlan existingMealPlan = getMealPlanById(id);

        existingMealPlan.setMealDate(mealPlan.getMealDate());
        existingMealPlan.setMealType(mealPlan.getMealType());

        if (mealPlan.getUser() != null
                && mealPlan.getUser().getId() != null) {

            User user = userRepository.findById(
                    mealPlan.getUser().getId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "User not found with id: "
                                    + mealPlan.getUser().getId()
                    ));

            existingMealPlan.setUser(user);
        }

        if (mealPlan.getRecipe() != null
                && mealPlan.getRecipe().getId() != null) {

            Recipe recipe = recipeRepository.findById(
                    mealPlan.getRecipe().getId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Recipe not found with id: "
                                    + mealPlan.getRecipe().getId()
                    ));

            existingMealPlan.setRecipe(recipe);
        }

        return mealPlanRepository.save(existingMealPlan);
    }

    public void deleteMealPlan(Long id) {

        MealPlan existingMealPlan = getMealPlanById(id);

        mealPlanRepository.delete(existingMealPlan);
    }
}