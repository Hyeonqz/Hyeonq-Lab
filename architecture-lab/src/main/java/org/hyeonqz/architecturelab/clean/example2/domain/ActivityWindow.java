package org.hyeonqz.architecturelab.clean.example2.domain;

import java.util.ArrayList;
import java.util.List;

public class ActivityWindow {
    private final List<Activity> activities;

    public ActivityWindow(List<Activity> activities) {
        this.activities = new ArrayList<>(activities);
    }

    public ActivityWindow() {
        this.activities = new ArrayList<>();
    }

    public void addActivity(Activity activity) {
        this.activities.add(activity);
    }

    public List<Activity> getActivities() {
        return activities;
    }
}
