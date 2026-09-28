package com.example.recipebox.controller;

import com.example.recipebox.dto.ShoppingListItem;
import com.example.recipebox.service.ShoppingListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shopping-list")
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    public ShoppingListController(
            ShoppingListService shoppingListService) {

        this.shoppingListService = shoppingListService;
    }

    @GetMapping
    public ResponseEntity<List<ShoppingListItem>> getShoppingList(

            @RequestParam Long userId,

            @RequestParam LocalDate startDate,

            @RequestParam LocalDate endDate) {

        List<ShoppingListItem> shoppingList =
                shoppingListService.generateShoppingList(
                        userId,
                        startDate,
                        endDate
                );

        return ResponseEntity.ok(shoppingList);
    }
}