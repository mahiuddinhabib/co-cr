package com.habib.cocr.ui.schedule;

import android.util.Log;

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

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<Session>> getSessions() {
        return sessionsLiveData;
    }

    public LiveData<Session> getCurrentOrUpcomingSession() {
        return currentOrUpcomingSession;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }



    public void fetchSessions() {
        // get the current day of the week
        String[] days = new String[] {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
        String today = days[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1];

        db.collection("classes").document("class-0001")
                .collection("schedules").document("sunday") // change this to today after testing
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

                                                            // Sort the sessions by start time
//                                                            SimpleDateFormat format = new SimpleDateFormat("h:mma");
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

                                                            // Determine the current or upcoming session
                                                            Date now = new Date();
                                                            for (Session s : sessions) {
                                                                try {
                                                                    Date start = format.parse(currentDate + " " + s.getStarts());
                                                                    Date end = format.parse(currentDate + " " + s.getEnds());
                                                                    if (now.after(start) && now.before(end)) {
                                                                        currentOrUpcomingSession.setValue(s);
                                                                        // Log the current session for debugging purposes
                                                                        Log.d(TAG, "Inside current session block");
                                                                        break;
                                                                    } else if (now.before(start)) {
                                                                        currentOrUpcomingSession.setValue(s);
                                                                        // Log the upcoming session for debugging purposes
                                                                        Log.d(TAG, "Inside upcoming session block");
                                                                        break;
                                                                    }

                                                                    // Log the current time for debugging purposes
                                                                    Log.d(TAG, "Outside logical block :" + now.after(start) + " " + now.before(end) + ", Now: " + now + ", Starts: " + start + ", Ends: " + end);

                                                                } catch (ParseException e) {
                                                                    Log.e(TAG, "Error parsing time", e);
                                                                }
                                                            }
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