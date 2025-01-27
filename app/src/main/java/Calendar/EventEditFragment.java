package Calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class EventEditFragment extends Fragment {

    private EditText eventNameET;
    private TextView eventDateTV, eventTimeTV;
    private LocalTime time;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_event_edit, container, false);

        eventNameET = view.findViewById(R.id.eventNameET);
        eventDateTV = view.findViewById(R.id.eventDateTV);
        eventTimeTV = view.findViewById(R.id.eventTimeTV);
        Button addIngredientBtn = view.findViewById(R.id.addIngredientBtn);
        Button saveEventBtn = view.findViewById(R.id.saveEventBtn);

        time = LocalTime.now();
        eventDateTV.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        eventTimeTV.setText("Time: " + CalendarUtils.formattedTime(time));

        // Obsługa zapisywania wydarzenia
        addIngredientBtn.setOnClickListener(v -> setupIngredientList(view));
        saveEventBtn.setOnClickListener(v -> saveEventAction());

        return view;
    }

    private void saveEventAction()
    {
        String eventName = eventNameET.getText().toString();
        if (!eventName.isEmpty())
        {
            Meal newEvent = new Meal(eventName, CalendarUtils.selectedDate, time);
            Meal.eventsList.add(newEvent);

            // Powrót do poprzedniego fragmentu lub zamknięcie obecnego
            requireActivity().getSupportFragmentManager().popBackStack();
        } else
            eventNameET.setError("Event name cannot be empty");
    }

    private void setupIngredientList(View view)
    {
        ListView ingredientListView = view.findViewById(R.id.ingredientListView);
        List<Ingredient> ingredients = new ArrayList<>();

        IngredientAdapter adapter = new IngredientAdapter(requireContext(), ingredients);
        ingredientListView.setAdapter(adapter);

        Button addIngredientBtn = view.findViewById(R.id.addIngredientBtn);
        EditText ingredientNameET = view.findViewById(R.id.ingredientNameET);

        addIngredientBtn.setOnClickListener(v -> {
            String ingredientName = ingredientNameET.getText().toString();
            if (!ingredientName.isEmpty()) {
                ingredients.add(new Ingredient(ingredientName, 100, 10, 20, 5)); // Przykładowe dane
                adapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(ingredientListView);
                ingredientNameET.setText("");
            }
        });
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

