package com.habib.cocr.ui.schedule;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.habib.cocr.model.Course;
import com.habib.cocr.model.Session;
import com.habib.cocr.model.Venue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScheduleViewModel extends ViewModel {

    private static final String TAG = "ScheduleViewModel";
    private final MutableLiveData<List<Session>> sessionsLiveData = new MutableLiveData<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<Session>> getSessions() {
        return sessionsLiveData;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }



    public void fetchSessions() {
        db.collection("classes").document("class-0001")
                .collection("schedules").document("saturday")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        // For debugging purposes, log the document data
//                        for (Map.Entry<String, Object> entry : document.getData().entrySet()) {
//                            Log.d(TAG, entry.getKey() + " => " + entry.getValue());
//                        }

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

                                    // Log the session data for debugging purposes
//                                    Log.d(TAG, "Session: " + session.getCourseId() + " " + session.getStarts() + " " + session.getEnds() + " " + session.getVenueId() + " " + session.isCancelled());

                                    db.collection("departments").document("dept-0001")
                                            .collection("courses").document(session.getCourseId())
                                            .get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                Course course = documentSnapshot.toObject(Course.class);
                                                session.setCourse(course);

                                                // Log the course data for debugging purposes
//                                                Log.d(TAG, "Course: " + course.getCourseTitle() + " " + course.getCourseCode() + " " + course.getCourseCredit() + " " + course.getCourseTeacherId());

                                                db.collection("departments").document("dept-0001")
                                                        .collection("venues").document(session.getVenueId())
                                                        .get()
                                                        .addOnSuccessListener(documentSnapshot2 -> {
                                                            Venue venue = documentSnapshot2.toObject(Venue.class);
                                                            session.setVenue(venue);

                                                            // Log the venue data for debugging purposes
//                                                            Log.d(TAG, "Venue: " + venue.getName());

                                                            sessions.add(session);
                                                            sessionsLiveData.setValue(sessions);
                                                        });
                                            });
                                }
                            }

                        }
                        else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                            errorMessage.setValue("Error getting documents: " + task.getException().getMessage());                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                        errorMessage.setValue("Error getting documents: " + task.getException().getMessage());                    }

                });
    }
}