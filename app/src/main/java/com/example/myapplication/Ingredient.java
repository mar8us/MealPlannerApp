package com.example.myapplication;

public class Ingredient
{
    private String name;
    private int calories;
    private int protein;
    private int carbs;
    private int fats;

    public Ingredient(String name, int calories, int protein, int carbs, int fats) {
        this.name = name;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
    }

    public String getName() { return name; }
    public int getCalories() { return calories; }
    public int getProtein() { return protein; }
    public int getCarbs() { return carbs; }
    public int getFats() { return fats; }
}