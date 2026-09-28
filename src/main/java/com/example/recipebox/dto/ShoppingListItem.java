package com.example.recipebox.dto;

public class ShoppingListItem {

    private String ingredientName;
    private Double quantity;
    private String unit;

    public ShoppingListItem() {
    }

    public ShoppingListItem(
            String ingredientName,
            Double quantity,
            String unit) {

        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}