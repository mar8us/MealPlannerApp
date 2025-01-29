package Calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarMonthFragment extends Fragment implements CalendarAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar_month, container, false);

        initWidgets(view);
        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();

        // Set click listeners for the buttons
        view.findViewById(R.id.previousMonthBtn).setOnClickListener(v -> previousMonthAction());
        view.findViewById(R.id.nextMonthBtn).setOnClickListener(v -> nextMonthAction());

        return view;
    }

    private void initWidgets(View view) {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
    }

    private void setMonthView() {
        monthYearText.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = CalendarUtils.daysInMonthArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    private void previousMonthAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    private void nextMonthAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        if (date != null) {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
//        requireActivity().getSupportFragmentManager().popBackStack();
        // Przejście do CalendarWeekFragment
        CalendarWeekFragment weekFragment = new CalendarWeekFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, weekFragment)
                .addToBackStack(null)
                .commit();
        // Możesz zmienić sposób przejścia do innego ekranu w fragmentach, np. używając Navigation Component
        // startActivity(new Intent(getContext(), EventEditActivity.class));
    }
}
