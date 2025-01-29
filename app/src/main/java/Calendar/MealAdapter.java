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

public class MealAdapter extends ArrayAdapter<Meal>
{
    public MealAdapter(@NonNull Context context, List<Meal> events)
    {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Meal meal = getItem(position);

        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.meal_cell, parent, false);
        }

        TextView mealNameTV = convertView.findViewById(R.id.mealNameTV);
        TextView mealProteinTV = convertView.findViewById(R.id.mealProteinTV);
        TextView mealCarbsTV = convertView.findViewById(R.id.mealCarbsTV);
        TextView mealFatsTV = convertView.findViewById(R.id.mealFatsTV);
        TextView mealCaloriesTV = convertView.findViewById(R.id.mealCaloriesTV);

        // Nazwa i kategoria
        String nameWithCategory = meal.getName() + " • " + meal.getCategory();
        mealNameTV.setText(nameWithCategory);

        // Makroskładniki
        mealProteinTV.setText(String.format("P: %.1fg", meal.getTotalProtein()));
        mealCarbsTV.setText(String.format("C: %.1fg", meal.getTotalCarbs()));
        mealFatsTV.setText(String.format("F: %.1fg", meal.getTotalFats()));

        // Kalorie
        mealCaloriesTV.setText(String.format("%.0f kcal", meal.getTotalCalories()));

        return convertView;
    }
}

