package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class IngredientAdapter extends ArrayAdapter<Ingredient> {
    public IngredientAdapter(@NonNull Context context, List<Ingredient> ingredients) {
        super(context, 0, ingredients);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Ingredient ingredient = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ingredient_cell, parent, false);
        }

        TextView nameTV = convertView.findViewById(R.id.ingredientNameTV);
        TextView caloriesTV = convertView.findViewById(R.id.caloriesTV);
        TextView proteinTV = convertView.findViewById(R.id.proteinTV);
        TextView carbsTV = convertView.findViewById(R.id.carbsTV);
        TextView fatsTV = convertView.findViewById(R.id.fatsTV);

        nameTV.setText(ingredient.getName());
        caloriesTV.setText("Calories: " + ingredient.getCalories());
        proteinTV.setText("Protein: " + ingredient.getProtein());
        carbsTV.setText("Carbs: " + ingredient.getCarbs());
        fatsTV.setText("Fats: " + ingredient.getFats());

        return convertView;
    }
}

