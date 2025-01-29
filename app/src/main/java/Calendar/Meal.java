package Calendar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Meal {
    // Lista wszystkich posiłków dla lokalnego zarządzania
    public static ArrayList<Meal> eventsList = new ArrayList<>();

    // Metoda do filtrowania posiłków po dacie
    public static ArrayList<Meal> eventsForDate(LocalDate date) {
        ArrayList<Meal> events = new ArrayList<>();
        for(Meal event : eventsList) {
            if(event.getDate().equals(date))
                events.add(event);
        }
        return events;
    }

    // Pola klasy
    private String id;  // Firestore document ID
    private String userId;
    private String name;
    private String category;
    private LocalDate date;
    private LocalTime time;
    private String recipe;
    private List<Ingredient> ingredients;
    private double totalCalories;
    private double totalProtein;
    private double totalCarbs;
    private double totalFats;

    // Konstruktor
    public Meal(String userId, String name, String category, LocalDate date,
                LocalTime time, String recipe) {
        this.userId = userId;
        this.name = name;
        this.category = category;
        this.date = date;
        this.time = time;
        this.recipe = recipe;
        this.ingredients = new ArrayList<>();
        //eventsList.add(this); // Automatycznie dodaj do lokalnej listy
    }

    // Pusty konstruktor dla Firestore
    public Meal() {
        this.ingredients = new ArrayList<>();
    }

    // Metody do zarządzania składnikami
    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        calculateNutrition();
    }

    public void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
        calculateNutrition();
    }

    private void calculateNutrition() {
        totalCalories = ingredients.stream().mapToDouble(i -> i.getCalories()).sum();
        totalProtein = ingredients.stream().mapToDouble(i -> i.getProtein()).sum();
        totalCarbs = ingredients.stream().mapToDouble(i -> i.getCarbs()).sum();
        totalFats = ingredients.stream().mapToDouble(i -> i.getFats()).sum();
    }

    // Konwersja do Map dla Firestore
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("name", name);
        map.put("category", category);
        map.put("date", date.toString());
        map.put("time", time.toString());
        map.put("recipe", recipe);
        map.put("ingredients", ingredients.stream()
                .map(Ingredient::toMap)
                .collect(Collectors.toList()));
        map.put("totalCalories", totalCalories);
        map.put("totalProtein", totalProtein);
        map.put("totalCarbs", totalCarbs);
        map.put("totalFats", totalFats);
        return map;
    }

    // Gettery i settery
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getRecipe() { return recipe; }
    public void setRecipe(String recipe) { this.recipe = recipe; }

    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        calculateNutrition();
    }

    public double getTotalCalories() { return totalCalories; }
    public double getTotalProtein() { return totalProtein; }
    public double getTotalCarbs() { return totalCarbs; }
    public double getTotalFats() { return totalFats; }
}