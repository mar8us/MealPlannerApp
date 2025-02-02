package Calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Firebase.MealManager;

public class CalendarWeekFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;

    private MealManager mealManager;
    private ListenerRegistration mealsListener;
    private TextView totalCaloriesLabel;
    private TextView totalProteinLabel;
    private TextView totalCarbsLabel;
    private TextView totalFatLabel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_calendar_week, container, false);

        // Inicjalizacja MealManager
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mealManager = new MealManager(userId);

        initWidgets(view);
        if(CalendarUtils.selectedDate == null)
            CalendarUtils.selectedDate = LocalDate.now();
        setWeekView();
        loadMealsForSelectedDate();

        // Obsługa kliknięć dla przycisków
        view.findViewById(R.id.previousWeekBtn).setOnClickListener(v -> previousWeekAction());
        view.findViewById(R.id.nextWeekBtn).setOnClickListener(v -> nextWeekAction());
        view.findViewById(R.id.newEventBtn).setOnClickListener(v -> newEventAction());
        return view;
    }

    private void loadMealsForSelectedDate()
    {
        if (mealsListener != null)
            mealsListener.remove();

        mealsListener = mealManager.addDayMealsListener(CalendarUtils.selectedDate,
                new MealManager.OnDayMealsListener()
                {
                    @Override
                    public void onMealsUpdated(List<Meal> meals)
                    {
                        Meal.eventsList.clear();
                        Meal.eventsList.addAll(meals);
                        setEventAdapter();
                    }

                    @Override
                    public void onError(Exception e)
                    {
                        Toast.makeText(getContext(),
                                "Error loading meals: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initWidgets(View view)
    {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
        eventListView = view.findViewById(R.id.eventListView);
        totalCaloriesLabel = view.findViewById(R.id.totalCaloriesLabel);
        totalProteinLabel = view.findViewById(R.id.totalProteinLabel);
        totalFatLabel = view.findViewById(R.id.totalFatLabel);
        totalCarbsLabel = view.findViewById(R.id.totalCarbsLabel);
    }

    private void setWeekView()
    {
        monthYearText.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = CalendarUtils.daysInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setEventAdapter();
    }

    private void previousWeekAction()
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    private void nextWeekAction()
    {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    private void newEventAction()
    {
        EventEditFragment.showEventEdit(this);
    }

    private void editEventAction(Meal meal)
    {
        EventEditFragment eventEditFragment = new EventEditFragment();
        Bundle args = new Bundle();
        args.putSerializable("meal", meal);
        eventEditFragment.setArguments(args);

        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, eventEditFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onItemClick(int position, LocalDate date)
    {
        CalendarUtils.selectedDate = date;
        setWeekView();
        loadMealsForSelectedDate();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if(mealsListener != null)
            mealsListener.remove();

    }

    @Override
    public void onResume()
    {
        super.onResume();
        setEventAdapter();
    }

    private void setEventAdapter()
    {
        ArrayList<Meal> dailyEvents = Meal.eventsForDate(CalendarUtils.selectedDate);
        MealAdapter mealAdapter = new MealAdapter(requireContext(), dailyEvents);

        Meal.DayNutrition nutrition = Meal.calculateDayNutrition(dailyEvents);
        totalCaloriesLabel.setText(String.format("Kalorie: %.0f kcal", nutrition.totalCalories));
        totalProteinLabel.setText(String.format("Białko: %.1f g", nutrition.totalProtein));
        totalCarbsLabel.setText(String.format("Węgl.: %.1f g", nutrition.totalCarbs));
        totalFatLabel.setText(String.format("Tłuszcze: %.1f g", nutrition.totalFats));

        eventListView.setAdapter(mealAdapter);
        eventListView.setOnItemClickListener((parent, view, position, id) -> {
            Meal selectedMeal = dailyEvents.get(position);
            editEventAction(selectedMeal);
        });
    }
}
