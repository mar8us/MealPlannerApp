package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class ShortsFragment extends Fragment {

    private boolean isWeeklyView = true; // Domyślny widok to kalendarz tygodniowy
    private Button toggleViewBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_shorts, container, false);

        toggleViewBtn = view.findViewById(R.id.toggleViewBtn);

        loadFragment(new CalendarWeekFragment());

        toggleViewBtn.setOnClickListener(v ->
        {
            if (isWeeklyView)
            {
                loadFragment(new CalendarMonthFragment());
                toggleViewBtn.setText("Switch to Weekly View");
            }
            else
            {
                loadFragment(new CalendarWeekFragment());
                toggleViewBtn.setText("Switch to Monthly View");
            }
            isWeeklyView = !isWeeklyView;
        });
        requireActivity().getSupportFragmentManager().addOnBackStackChangedListener(this::handleBackStackChanges);
        return view;
    }

    // Obsługa zmian w stosie fragmentów
    private void handleBackStackChanges() {
        Fragment currentFragment = requireActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);

        if (currentFragment instanceof EventEditFragment) {
            // Ukryj przycisk
            toggleViewBtn.setVisibility(View.GONE);
        } else {
            // Pokaż przycisk
            toggleViewBtn.setVisibility(View.VISIBLE);
        }
    }

    private void loadFragment(Fragment fragment)
    {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}