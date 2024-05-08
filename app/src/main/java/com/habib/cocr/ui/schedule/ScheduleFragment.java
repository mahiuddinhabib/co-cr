package com.habib.cocr.ui.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habib.cocr.R;
import com.habib.cocr.databinding.FragmentScheduleBinding;

public class ScheduleFragment extends Fragment {
    private ScheduleViewModel scheduleViewModel;
    private RecyclerView scheduleRecyclerView;
    private ScheduleAdapter scheduleAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_schedule, container, false);
        scheduleRecyclerView = root.findViewById(R.id.schedule_recycler_view);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        scheduleViewModel.getSessions().observe(getViewLifecycleOwner(), sessions -> {
            scheduleAdapter = new ScheduleAdapter(sessions);
            scheduleRecyclerView.setAdapter(scheduleAdapter);
        });

        // Observe the errorMessages LiveData
        scheduleViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        scheduleViewModel.fetchSessions();

        return root;
    }
}