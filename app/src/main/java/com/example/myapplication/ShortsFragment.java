package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import Calendar.CalendarMonthFragment;
import Calendar.CalendarWeekFragment;
import Calendar.EventEditFragment;


public class ShortsFragment extends Fragment {
    private boolean isWeeklyView = true;
    private Button toggleViewBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shorts, container, false);
        toggleViewBtn = view.findViewById(R.id.toggleViewBtn);

        // Sprawdzamy czy nie przywracamy po rotacji
        if (savedInstanceState == null) {
            loadFragment(new CalendarWeekFragment(), false);
        }

        toggleViewBtn.setOnClickListener(v -> {
            if (isWeeklyView)
            {
                loadFragment(new CalendarMonthFragment(), true);
                toggleViewBtn.setText(R.string.week_view);
            }
            else
            {
                loadFragment(new CalendarWeekFragment(), true);
                toggleViewBtn.setText(R.string.monthly_view);
            }
            isWeeklyView = !isWeeklyView;
        });

        // Przenosimy słuchacza do onViewCreated
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Używamy getChildFragmentManager() zamiast activity.getSupportFragmentManager()
        getChildFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getChildFragmentManager()
                    .findFragmentById(R.id.fragmentContainer);

            if (currentFragment instanceof EventEditFragment) {
                toggleViewBtn.setVisibility(View.GONE);
            } else {
                toggleViewBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Czyścimy listener przy zniszczeniu widoku
        getChildFragmentManager().removeOnBackStackChangedListener(() -> {});
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment);

        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }
}