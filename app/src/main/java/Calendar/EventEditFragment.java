package Calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

public class EventEditFragment extends Fragment {

    private Meal mealToEdit;
    private EditText eventNameET;
    private TextView eventDateTV, eventTimeTV;
    private LocalTime time;
    private Spinner mealCategorySpinner;
    private MultiAutoCompleteTextView recipeET;
    private EditText caloriesET, proteinET, carbsET, fatsET;
    private List<Ingredient> ingredients;
    private IngredientAdapter adapter;
    private String currentUserId; // ID zalogowanego użytkownika

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_event_edit, container, false);

        // Inicjalizacja FirebaseAuth
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Inicjalizacja komponentów UI
        initializeViews(view);
        setupIngredientList(view);

        // Czy dane do edycji
        Bundle args = getArguments();
        if(args != null && args.getSerializable("meal") != null)
            mealToEdit = (Meal) args.getSerializable("meal");

        if(mealToEdit != null)
            fillFieldsWithMealData();
        return view;
    }

    private void initializeViews(View view)
    {
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

    private void fillFieldsWithMealData() {
        eventNameET.setText(mealToEdit.getName());
        time = mealToEdit.getTime();
        CalendarUtils.selectedDate = mealToEdit.getDate();

        eventDateTV.setText("Date: " + CalendarUtils.formattedDate(mealToEdit.getDate()));
        eventTimeTV.setText("Time: " + CalendarUtils.formattedTime(mealToEdit.getTime()));

        // Ustaw kategorię w Spinnerze
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) mealCategorySpinner.getAdapter();
        int position = adapter.getPosition(mealToEdit.getCategory());
        mealCategorySpinner.setSelection(position);

        recipeET.setText(mealToEdit.getRecipe());

        // Dodaj składniki
        ingredients.clear();
        ingredients.addAll(mealToEdit.getIngredients());
        adapter.notifyDataSetChanged();
    }

    private void addIngredient()
    {
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

    private void scanBarcode() {
        // Tu implementacja skanowania kodu kreskowego
        // Po zeskanowaniu wywołaj getProduct(barcode)
    }

    private void saveEventAction()
    {
        String eventName = eventNameET.getText().toString();
        String category = mealCategorySpinner.getSelectedItem().toString();
        String recipe = recipeET.getText().toString();

        if (eventName.isEmpty())
        {
            eventNameET.setError("Event name cannot be empty");
            return;
        }

        if (mealToEdit != null)
        {
            // Aktualizacja istniejącego posiłku
            int index = Meal.eventsList.indexOf(mealToEdit);
            mealToEdit.setName(eventName);
            mealToEdit.setCategory(category);
            mealToEdit.setDate(CalendarUtils.selectedDate);
            mealToEdit.setTime(time);
            mealToEdit.setRecipe(recipe);
            mealToEdit.getIngredients().clear();
            for (Ingredient ingredient : ingredients)
                mealToEdit.addIngredient(ingredient);
            if (index != -1)
                Meal.eventsList.set(index, mealToEdit);
        }
        else
        {
            // Tworzenie nowego posiłku
            Meal newMeal = new Meal(currentUserId, eventName, category, CalendarUtils.selectedDate, time, recipe);
            for (Ingredient ingredient : ingredients)
                newMeal.addIngredient(ingredient);
            Meal.eventsList.add(newMeal);
        }
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void setupIngredientList(View view) {
        ListView ingredientListView = view.findViewById(R.id.ingredientListView);
        ingredients = new ArrayList<>();
        adapter = new IngredientAdapter(requireContext(), ingredients);
        ingredientListView.setAdapter(adapter);
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

    private double parseDouble(EditText editText) {
        String text = editText.getText().toString();
        try {
            return text.isEmpty() ? 0 : Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void onResume()
    {
        super.onResume();
        if(mealToEdit != null)
        {
            ListView listView = getView().findViewById(R.id.ingredientListView);
            setListViewHeightBasedOnChildren(listView);
        }
    }
}

