package com.habib.cocr.ui.schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.habib.cocr.R;
import com.habib.cocr.model.Schedule;
import com.habib.cocr.model.Session;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private List<Session> scheduleList;

    public ScheduleAdapter(List<Session> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_card, parent, false);
        return new ScheduleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        Session session = scheduleList.get(position);
        holder.courseTitle.setText(session.getCourse().getCourseTitle());
        holder.courseTime.setText(session.getStarts() + " - " + session.getEnds());
        holder.courseVenue.setText(session.getVenue().getName());
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        public TextView courseTitle;
        public TextView courseTime;
        public TextView courseVenue;

        public ScheduleViewHolder(View view) {
            super(view);
            courseTitle = view.findViewById(R.id.course_name);
            courseTime = view.findViewById(R.id.course_time);
            courseVenue = view.findViewById(R.id.course_venue);
        }
    }
}