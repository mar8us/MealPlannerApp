package Calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import Firebase.UserManager;
import models.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import api.OpenFoodFactsApi;
import api.RetrofitClient;

public class EventEditFragment extends Fragment {
    private EditText eventNameET;
    private TextView eventDateTV, eventTimeTV;
    private LocalTime time;
    private Spinner mealCategorySpinner;
    private MultiAutoCompleteTextView recipeET;
    private EditText caloriesET, proteinET, carbsET, fatsET;
    private List<Ingredient> ingredients;
    private IngredientAdapter adapter;
    private String currentUserId; // ID zalogowanego użytkownika

    OpenFoodFactsApi api = RetrofitClient.getRetrofitInstance().create(OpenFoodFactsApi.class);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_edit, container, false);

        // Inicjalizacja FirebaseAuth
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Inicjalizacja komponentów UI
        initializeViews(view);
        setupIngredientList(view);

        return view;
    }

    private void initializeViews(View view) {
        eventNameET = view.findViewById(R.id.eventNameET);
        eventDateTV = view.findViewById(R.id.eventDateTV);
        eventTimeTV = view.findViewById(R.id.eventTimeTV);
        mealCategorySpinner = view.findViewById(R.id.mealCategorySpinner);
        recipeET = view.findViewById(R.id.recipeET);
        caloriesET = view.findViewById(R.id.caloriesET);
        proteinET = view.findViewById(R.id.proteinET);
        carbsET = view.findViewById(R.id.carbsET);
        fatsET = view.findViewById(R.id.fatsET);

        Button addIngredientBtn = view.findViewById(R.id.addIngredientBtn);
        Button scanProductBtn = view.findViewById(R.id.scanProductBtn);
        Button saveEventBtn = view.findViewById(R.id.saveEventBtn);

        time = LocalTime.now();
        eventDateTV.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        eventTimeTV.setText("Time: " + CalendarUtils.formattedTime(time));

        addIngredientBtn.setOnClickListener(v -> addIngredient());
        scanProductBtn.setOnClickListener(v -> scanBarcode());
        saveEventBtn.setOnClickListener(v -> saveEventAction());
    }

    private void saveEventAction() {
        String eventName = eventNameET.getText().toString();
        String category = mealCategorySpinner.getSelectedItem().toString();
        String recipe = recipeET.getText().toString();

        if (eventName.isEmpty()) {
            eventNameET.setError("Event name cannot be empty");
            return;
        }

        // Tworzenie nowego posiłku z wszystkimi danymi
        Meal newMeal = new Meal(
                currentUserId,
                eventName,
                category,
                CalendarUtils.selectedDate,
                time,
                recipe
        );

        // Dodawanie składników
        for (Ingredient ingredient : ingredients) {
            newMeal.addIngredient(ingredient);
        }

        // Zapisywanie do Firestore
        MealManager mealManager = new MealManager();
        mealManager.addMeal(newMeal, new UserManager.FirebaseFirestoreCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(getContext(), "Meal saved successfully", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addIngredient() {
        EditText ingredientNameET = getView().findViewById(R.id.ingredientNameET);
        String ingredientName = ingredientNameET.getText().toString();

        if (!ingredientName.isEmpty()) {
            // Pobranie wartości makroskładników z pól
            double calories = parseDouble(caloriesET);
            double protein = parseDouble(proteinET);
            double carbs = parseDouble(carbsET);
            double fats = parseDouble(fatsET);

            Ingredient ingredient = new Ingredient(ingredientName, calories, protein, carbs, fats);
            ingredients.add(ingredient);
            adapter.notifyDataSetChanged();

            // Reset pól
            ingredientNameET.setText("");
            caloriesET.setText("");
            proteinET.setText("");
            carbsET.setText("");
            fatsET.setText("");

            setListViewHeightBasedOnChildren(getView().findViewById(R.id.ingredientListView));
        }
    }

    private double parseDouble(EditText editText) {
        String text = editText.getText().toString();
        try {
            return text.isEmpty() ? 0 : Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void setupIngredientList(View view) {
        ListView ingredientListView = view.findViewById(R.id.ingredientListView);
        ingredients = new ArrayList<>();
        adapter = new IngredientAdapter(requireContext(), ingredients);
        ingredientListView.setAdapter(adapter);
    }

    private void scanBarcode() {
        // Tu implementacja skanowania kodu kreskowego
        // Po zeskanowaniu wywołaj getProduct(barcode)
    }

    private void getProduct(String barcode) {
        api.getProduct(barcode, "en").enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Product product = response.body();
                    // Uzupełnij pola na podstawie danych z API
                    updateNutritionFields(product);
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Toast.makeText(getContext(), "Error loading product: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateNutritionFields(Product product) {
        // Uzupełnij pola na podstawie danych z produktu
        caloriesET.setText(String.valueOf(product.getNutriments().getEnergyKcal()));
        proteinET.setText(String.valueOf(product.getNutriments().getProteins()));
        carbsET.setText(String.valueOf(product.getNutriments().getCarbohydrates()));
        fatsET.setText(String.valueOf(product.getNutriments().getFat()));
    }

    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if(listAdapter == null)
            return;
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}

