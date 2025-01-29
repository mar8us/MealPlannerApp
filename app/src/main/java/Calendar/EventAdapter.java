package Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;

import java.util.List;

public class EventAdapter extends ArrayAdapter<Meal>
{
    public EventAdapter(@NonNull Context context, List<Meal> events)
    {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        Meal meal = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.meal_cell, parent, false);

        TextView mealCellTV = convertView.findViewById(R.id.mealCellTV);

        String eventTitle = meal.getName() +" "+ CalendarUtils.formattedTime(meal.getTime());
        mealCellTV.setText(eventTitle);
        return convertView;
    }
}

