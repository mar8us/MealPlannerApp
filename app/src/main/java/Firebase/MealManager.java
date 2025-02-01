package Firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Calendar.Ingredient;
import Calendar.Meal;

public class MealManager {
    private final FirebaseFirestore db;
    private final String userId;

    // Stałe dla nazw kolekcji
    private static final String USERS_COLLECTION = "users";
    private static final String MEALS_COLLECTION = "meals";

    public MealManager(String userId) {
        this.db = FirebaseFirestore.getInstance();
        this.userId = userId;
    }

    // Zapis pojedynczego posiłku
    public Task<Void> saveMeal(Meal meal) {
        String dateStr = meal.getDate().toString(); // Format: YYYY-MM-DD
        DocumentReference mealRef = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(MEALS_COLLECTION)
                .document(dateStr)
                .collection("dayMeals")
                .document(); // Automatyczne ID

        meal.setId(mealRef.getId()); // Zapisz ID dokumentu w obiekcie
        return mealRef.set(meal.toMap());
    }

    // Zapis wszystkich posiłków z danego dnia
    public Task<Void> saveDayMeals(LocalDate date, List<Meal> meals) {
        WriteBatch batch = db.batch();
        String dateStr = date.toString();

        CollectionReference dayMealsRef = db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(MEALS_COLLECTION)
                .document(dateStr)
                .collection("dayMeals");

        // Najpierw usuń wszystkie istniejące posiłki z tego dnia
        return dayMealsRef.get()
                .continueWithTask(task -> {
                    WriteBatch deleteBatch = db.batch();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        deleteBatch.delete(document.getReference());
                    }
                    return deleteBatch.commit();
                })
                .continueWithTask(task -> {
                    // Następnie zapisz nowe posiłki
                    WriteBatch saveBatch = db.batch();
                    for (Meal meal : meals) {
                        DocumentReference mealRef = dayMealsRef.document();
                        meal.setId(mealRef.getId());
                        saveBatch.set(mealRef, meal.toMap());
                    }
                    return saveBatch.commit();
                });
    }

    // Pobranie posiłków z danego dnia
    public Task<List<Meal>> getDayMeals(LocalDate date) {
        String dateStr = date.toString();
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(MEALS_COLLECTION)
                .document(dateStr)
                .collection("dayMeals")
                .get()
                .continueWith(task -> {
                    List<Meal> meals = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Meal meal = documentToMeal(document);
                        meals.add(meal);
                    }
                    return meals;
                });
    }

    // Aktualizacja posiłku
    public Task<Void> updateMeal(Meal meal) {
        if (meal.getId() == null) {
            throw new IllegalArgumentException("Meal ID cannot be null for update operation");
        }

        String dateStr = meal.getDate().toString();
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(MEALS_COLLECTION)
                .document(dateStr)
                .collection("dayMeals")
                .document(meal.getId())
                .set(meal.toMap());
    }

    // Usunięcie posiłku
    public Task<Void> deleteMeal(Meal meal) {
        if (meal.getId() == null) {
            throw new IllegalArgumentException("Meal ID cannot be null for delete operation");
        }

        String dateStr = meal.getDate().toString();
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(MEALS_COLLECTION)
                .document(dateStr)
                .collection("dayMeals")
                .document(meal.getId())
                .delete();
    }

    // Konwersja dokumentu Firestore na obiekt Meal
    private Meal documentToMeal(DocumentSnapshot document) {
        Map<String, Object> data = document.getData();
        if (data == null) return null;

        // Tworzenie nowego obiektu Meal
        Meal meal = new Meal();
        meal.setId(document.getId());
        meal.setUserId((String) data.get("userId"));
        meal.setName((String) data.get("name"));
        meal.setCategory((String) data.get("category"));
        meal.setDate(LocalDate.parse((String) data.get("date")));
        meal.setTime(LocalTime.parse((String) data.get("time")));
        meal.setRecipe((String) data.get("recipe"));

        // Konwersja składników
        List<Map<String, Object>> ingredientsData = (List<Map<String, Object>>) data.get("ingredients");
        if (ingredientsData != null) {
            List<Ingredient> ingredients = new ArrayList<>();
            for (Map<String, Object> ingredientData : ingredientsData) {
                Ingredient ingredient = new Ingredient(
                        (String) ingredientData.get("name"),
                        ((Number) ingredientData.get("calories")).doubleValue(),
                        ((Number) ingredientData.get("protein")).doubleValue(),
                        ((Number) ingredientData.get("carbs")).doubleValue(),
                        ((Number) ingredientData.get("fats")).doubleValue()
                );
                ingredients.add(ingredient);
            }
            meal.setIngredients(ingredients);
        }

        return meal;
    }

    // Nasłuchiwanie zmian w posiłkach z danego dnia
    public ListenerRegistration addDayMealsListener(LocalDate date, OnDayMealsListener listener) {
        String dateStr = date.toString();
        return db.collection(USERS_COLLECTION)
                .document(userId)
                .collection(MEALS_COLLECTION)
                .document(dateStr)
                .collection("dayMeals")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        listener.onError(error);
                        return;
                    }

                    List<Meal> meals = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : value) {
                        meals.add(documentToMeal(doc));
                    }
                    listener.onMealsUpdated(meals);
                });
    }

    // Interfejs dla nasłuchiwania zmian
    public interface OnDayMealsListener {
        void onMealsUpdated(List<Meal> meals);
        void onError(Exception e);
    }
}
