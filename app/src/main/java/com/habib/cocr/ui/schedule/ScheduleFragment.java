package com.habib.cocr.ui.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habib.cocr.R;
import com.habib.cocr.databinding.FragmentScheduleBinding;
import com.habib.cocr.model.Session;

public class ScheduleFragment extends Fragment {
    private RecyclerView scheduleRecyclerView;
    private ScheduleAdapter scheduleAdapter;
    private LinearLayout detailsSection;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_schedule, container, false);
        scheduleRecyclerView = root.findViewById(R.id.schedule_recycler_view);
        detailsSection = root.findViewById(R.id.details_section);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ScheduleViewModel scheduleViewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        scheduleViewModel.getSessions().observe(getViewLifecycleOwner(), sessions -> {
            scheduleAdapter = new ScheduleAdapter(sessions, new ScheduleAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Session session) {
                    // Update the details section with the clicked session's details
                    updateDetailsSection(session);
                }
            });
            scheduleRecyclerView.setAdapter(scheduleAdapter);
        });

        scheduleViewModel.getCurrentOrUpcomingSession().observe(getViewLifecycleOwner(), session -> {
            // Update the details section with the session's details
            updateDetailsSection(session);
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

    private void updateDetailsSection(Session session) {
        // Find the TextViews
        TextView courseNameTextView = getView().findViewById(R.id.course_name);
        TextView courseTimeTextView = getView().findViewById(R.id.course_time);
        TextView courseTeacherTextView = getView().findViewById(R.id.course_teacher);
        TextView courseVenueTextView = getView().findViewById(R.id.course_venue);

        // Update the TextViews with the session's details
        courseNameTextView.setText(session.getCourse().getCourseTitle());
        courseTimeTextView.setText(session.getStarts() + " - " + session.getEnds());
        courseTeacherTextView.setText(session.getCourse().getCourseTeacherId()); // Assuming this is the teacher's name
        courseVenueTextView.setText(session.getVenue().getName());
    }
}