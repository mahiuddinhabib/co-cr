package com.habib.cocr.ui.schedule;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.habib.cocr.R;
import com.habib.cocr.databinding.FragmentScheduleBinding;
import com.habib.cocr.model.Session;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleFragment extends Fragment {
    private static final String TAG = "ScheduleFragment";
    private RecyclerView scheduleRecyclerView;
    private ScheduleAdapter scheduleAdapter;
    private LinearLayout detailsSection;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_schedule, container, false);
        scheduleRecyclerView = root.findViewById(R.id.schedule_recycler_view);
        detailsSection = root.findViewById(R.id.details_section);
        scheduleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Find the ProgressBar and overlay
        ProgressBar progressBar = root.findViewById(R.id.progress_bar);
        FrameLayout overlay = root.findViewById(R.id.overlay);

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

        // Update the details section with the session's details
        scheduleViewModel.getCurrentOrUpcomingSession().observe(getViewLifecycleOwner(), this::updateDetailsSection);

        // Observe the errorMessages LiveData
        scheduleViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe the loading LiveData
        scheduleViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                overlay.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
                overlay.setVisibility(View.GONE);
            }
        });

        // Swipe to refresh
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            scheduleViewModel.fetchSessions();
            swipeRefreshLayout.setRefreshing(false);
        });

        scheduleViewModel.fetchSessions();

        return root;
    }

    private void updateDetailsSection(Session session) {
        // Find the TextViews
        TextView courseStatusTextView = getView().findViewById(R.id.course_status);
        TextView courseNameTextView = getView().findViewById(R.id.course_name);
        TextView courseTimeTextView = getView().findViewById(R.id.course_time);
        TextView courseTeacherTextView = getView().findViewById(R.id.course_teacher);
        TextView courseVenueTextView = getView().findViewById(R.id.course_venue);

        if(session != null){
            // Update the TextViews with the session's details
            courseNameTextView.setText(session.getCourse().getCourseTitle());
            courseTimeTextView.setText(session.getStarts() + " - " + session.getEnds());
            courseTeacherTextView.setText(session.getCourse().getCourseTeacherId()); // Assuming this is the teacher's name
            courseVenueTextView.setText(session.getVenue().getName());

            // Determine the status of the session
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd h:mma");
            try {
                Date now = new Date();
                Date start = format.parse(currentDate + " " + session.getStarts());
                Date end = format.parse(currentDate + " " + session.getEnds());

                if (now.before(start)) {
                    courseStatusTextView.setText("Upcoming");
                } else if (now.after(start) && now.before(end)) {
                    courseStatusTextView.setText("Ongoing");
                } else {
                    courseStatusTextView.setText("Finished");
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing time", e);
            }
        } else {
            // Update the TextViews for no more classes
            courseNameTextView.setText("No more classes...");
            courseTimeTextView.setText("Enjoy your day!");
            courseTeacherTextView.setText("");
            courseVenueTextView.setText("");
        }

    }
}