package com.mergington.activities.service;

import com.mergington.activities.model.Activity;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ActivityRepository {
    private final Map<String, Activity> activities = new HashMap<>();

    public ActivityRepository() {
        activities.put("Chess Club", new Activity(
                "Learn strategies and compete in chess tournaments",
                "Fridays, 3:30 PM - 5:00 PM",
                12,
                new ArrayList<>(List.of("michael@mergington.edu", "daniel@mergington.edu"))
        ));
        activities.put("Programming Class", new Activity(
                "Learn programming fundamentals and build software projects",
                "Tuesdays and Thursdays, 3:30 PM - 4:30 PM",
                20,
                new ArrayList<>(List.of("emma@mergington.edu", "sophia@mergington.edu"))
        ));
        activities.put("Gym Class", new Activity(
                "Physical education and sports activities",
                "Mondays, Wednesdays, Fridays, 2:00 PM - 3:00 PM",
                30,
                new ArrayList<>(List.of("john@mergington.edu", "olivia@mergington.edu"))
        ));
    }

    public Map<String, Activity> getAllActivities() {
        return Collections.unmodifiableMap(activities);
    }

    public Activity getActivity(String name) {
        return activities.get(name);
    }
}
