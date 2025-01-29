package Calendar;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Firebase.UserManager;

public class MealManager
{
    private FirebaseFirestore firestore;

    public MealManager() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void addMeal(Meal meal, UserManager.FirebaseFirestoreCallback callback) {
        firestore.collection("users")
                .document(meal.getUserId())
                .collection("meals")
                .add(meal.toMap())
                .addOnSuccessListener(documentReference -> {
                    meal.setId(documentReference.getId());
                    callback.onSuccess("Meal added successfully");
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void getMealsForDate(String userId, LocalDate date,
                                Firebase.MealManager.FirebaseFirestoreGetMealsCallback callback) {
        firestore.collection("users")
                .document(userId)
                .collection("meals")
                .whereEqualTo("date", date.toString())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Meal> meals = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Meal meal = doc.toObject(Meal.class);
                        meal.setId(doc.getId());
                        meals.add(meal);
                    }
                    callback.onSuccess(meals);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public void updateMeal(Meal meal, UserManager.FirebaseFirestoreCallback callback) {
        if (meal.getId() == null) {
            callback.onFailure("Meal ID is null");
            return;
        }

        firestore.collection("users")
                .document(meal.getUserId())
                .collection("meals")
                .document(meal.getId())
                .update(meal.toMap())
                .addOnSuccessListener(aVoid -> callback.onSuccess("Meal updated successfully"))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Metoda do pobierania posiłków z danej kategorii
    public void getMealsByCategory(String userId, String category,
                                   Firebase.MealManager.FirebaseFirestoreGetMealsCallback callback) {
        firestore.collection("users")
                .document(userId)
                .collection("meals")
                .whereEqualTo("category", category)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Meal> meals = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Meal meal = doc.toObject(Meal.class);
                        meal.setId(doc.getId());
                        meals.add(meal);
                    }
                    callback.onSuccess(meals);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    // Metoda do pobierania statystyk żywieniowych dla danego okresu
    public void getNutritionStats(String userId, LocalDate startDate, LocalDate endDate,
                                  FirebaseFirestoreGetNutritionStatsCallback callback) {
        firestore.collection("users")
                .document(userId)
                .collection("meals")
                .whereGreaterThanOrEqualTo("date", startDate.toString())
                .whereLessThanOrEqualTo("date", endDate.toString())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalCalories = 0;
                    double totalProtein = 0;
                    double totalCarbs = 0;
                    double totalFats = 0;

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Meal meal = doc.toObject(Meal.class);
                        totalCalories += meal.getTotalCalories();
                        totalProtein += meal.getTotalProtein();
                        totalCarbs += meal.getTotalCarbs();
                        totalFats += meal.getTotalFats();
                    }

                    Map<String, Double> stats = new HashMap<>();
                    stats.put("calories", totalCalories);
                    stats.put("protein", totalProtein);
                    stats.put("carbs", totalCarbs);
                    stats.put("fats", totalFats);

                    callback.onSuccess(stats);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public interface FirebaseFirestoreGetNutritionStatsCallback {
        void onSuccess(Map<String, Double> nutritionStats);
        void onFailure(String errorMessage);
    }
}