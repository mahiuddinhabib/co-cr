package com.habib.cocr.ui.schedule;

import android.util.Log;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.habib.cocr.model.Course;
import com.habib.cocr.model.Session;
import com.habib.cocr.model.Venue;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;


public class ScheduleViewModel extends ViewModel {

    private static final String TAG = "ScheduleViewModel";
    private final MutableLiveData<List<Session>> sessionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Session> currentOrUpcomingSession = new MutableLiveData<>();

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<List<Session>> getSessions() {
        return sessionsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Session> getCurrentOrUpcomingSession() {
        return currentOrUpcomingSession;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }



    public void fetchSessions() {
        isLoading.setValue(true);

        String[] days = new String[] {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
        String today = days[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1];

        db.collection("classes").document("class-0001")
                .collection("schedules").document("sunday")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<Session> sessions = new ArrayList<>();
                            List<Map<String, Object>> scheduleList = (List<Map<String, Object>>) document.get("schedule");
                            if (scheduleList != null) {
                                for (Map<String, Object> item : scheduleList) {
                                    Session session = new Session();
                                    session.setCourseId((String) item.get("courseId"));
                                    session.setStarts((String) item.get("starts"));
                                    session.setEnds((String) item.get("ends"));
                                    session.setVenueId((String) item.get("venueId"));
                                    session.setCancelled((Boolean) item.get("isCancelled"));

                                    db.collection("departments").document("dept-0001")
                                            .collection("courses").document(session.getCourseId())
                                            .get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                Course course = documentSnapshot.toObject(Course.class);
                                                session.setCourse(course);

                                                // Fetch the teacher's name
                                                db.collection("users").document(course.getCourseTeacherId())
                                                        .get()
                                                        .addOnSuccessListener(documentSnapshot2 -> {
                                                            String teacherName = documentSnapshot2.getString("name");
                                                            session.setCourseTeacherName(teacherName); // Set the teacher's name

                                                            db.collection("departments").document("dept-0001")
                                                                    .collection("venues").document(session.getVenueId())
                                                                    .get()
                                                                    .addOnSuccessListener(documentSnapshot3 -> {
                                                                        Venue venue = documentSnapshot3.toObject(Venue.class);
                                                                        session.setVenue(venue);

                                                                        sessions.add(session);

                                                                        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                                                                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd h:mma");
                                                                        Collections.sort(sessions, new Comparator<Session>() {
                                                                            @Override
                                                                            public int compare(Session s1, Session s2) {
                                                                                try {
                                                                                    Date time1 = format.parse(currentDate + " " + s1.getStarts());
                                                                                    Date time2 = format.parse(currentDate + " " + s2.getStarts());
                                                                                    return time1.compareTo(time2);
                                                                                } catch (ParseException e) {
                                                                                    throw new IllegalArgumentException(e);
                                                                                }
                                                                            }
                                                                        });
                                                                        sessionsLiveData.setValue(sessions);

                                                                        Date now = new Date();
                                                                        for (Session s : sessions) {
                                                                            try {
                                                                                Date start = format.parse(currentDate + " " + s.getStarts());
                                                                                Date end = format.parse(currentDate + " " + s.getEnds());
                                                                                if (now.after(start) && now.before(end)) {
                                                                                    currentOrUpcomingSession.setValue(s);
                                                                                    break;
                                                                                } else if (now.before(start)) {
                                                                                    currentOrUpcomingSession.setValue(s);
                                                                                    break;
                                                                                } else {
                                                                                    currentOrUpcomingSession.setValue(null);
                                                                                }
                                                                            } catch (ParseException e) {
                                                                                Log.e(TAG, "Error parsing time", e);
                                                                            }
                                                                        }
                                                                    });
                                                        });
                                            });
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            errorMessage.setValue("Error getting documents: " + task.getException().getMessage());
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        errorMessage.setValue("Error getting documents: " + task.getException().getMessage());
                    }

                    isLoading.setValue(false);
                });
    }
}