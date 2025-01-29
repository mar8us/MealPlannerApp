package Firebase;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Calendar.Meal;

public class MealManager {
    private FirebaseFirestore firestore;

    public MealManager() {
        firestore = FirebaseFirestore.getInstance();
    }

    // Dodawanie posiłku
    public void addMeal(Meal meal, UserManager.FirebaseFirestoreCallback callback)
    {
        firestore.collection("users")
                .document(meal.getUserId())
                .collection("meals")
                .add(meal.toMap())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess("Posiłek został dodany");
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    // Pobieranie posiłków dla konkretnej daty
    public void getMealsForDate(String userId, LocalDate date, FirebaseFirestoreGetMealsCallback callback)
    {
        firestore.collection("users")
                .document(userId)
                .collection("meals")
                .whereEqualTo("date", date.toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Meal> meals = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult()) {
                            meals.add(doc.toObject(Meal.class));
                        }
                        callback.onSuccess(meals);
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    // Interface dla callbacka z listą posiłków
    public interface FirebaseFirestoreGetMealsCallback {
        void onSuccess(List<Meal> meals);
        void onFailure(String errorMessage);
    }
}
