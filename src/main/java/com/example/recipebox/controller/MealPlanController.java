package com.example.recipebox.controller;

import com.example.recipebox.dto.MealPlanRequest;
import com.example.recipebox.entity.MealPlan;
import com.example.recipebox.entity.Recipe;
import com.example.recipebox.entity.User;
import com.example.recipebox.service.MealPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meal-plans")
public class MealPlanController {

    private final MealPlanService mealPlanService;

    public MealPlanController(MealPlanService mealPlanService) {
        this.mealPlanService = mealPlanService;
    }

    @PostMapping
    public ResponseEntity<MealPlan> createMealPlan(
            @Valid @RequestBody MealPlanRequest request) {

        User user = new User();
        user.setId(request.getUserId());

        Recipe recipe = new Recipe();
        recipe.setId(request.getRecipeId());

        MealPlan mealPlan = new MealPlan();

        mealPlan.setMealDate(request.getMealDate());
        mealPlan.setMealType(request.getMealType());
        mealPlan.setUser(user);
        mealPlan.setRecipe(recipe);

        MealPlan savedMealPlan =
                mealPlanService.createMealPlan(mealPlan);

        return new ResponseEntity<>(
                savedMealPlan,
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<MealPlan>> getAllMealPlans() {
        return ResponseEntity.ok(
                mealPlanService.getAllMealPlans()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<MealPlan> getMealPlanById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                mealPlanService.getMealPlanById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<MealPlan> updateMealPlan(
            @PathVariable Long id,
            @Valid @RequestBody MealPlanRequest request) {

        User user = new User();
        user.setId(request.getUserId());

        Recipe recipe = new Recipe();
        recipe.setId(request.getRecipeId());

        MealPlan mealPlan = new MealPlan();

        mealPlan.setMealDate(request.getMealDate());
        mealPlan.setMealType(request.getMealType());
        mealPlan.setUser(user);
        mealPlan.setRecipe(recipe);

        return ResponseEntity.ok(
                mealPlanService.updateMealPlan(id, mealPlan)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMealPlan(
            @PathVariable Long id) {

        mealPlanService.deleteMealPlan(id);

        return ResponseEntity.noContent().build();
    }
}