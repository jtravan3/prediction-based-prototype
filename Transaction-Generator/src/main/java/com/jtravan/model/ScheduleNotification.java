package com.jtravan.model;

/**
 * Created by johnravan on 1/11/17.
 */
public class ScheduleNotification {

    private ScheduleNotificationType scheduleNotificationType;
    private Schedule schedule;

    public ScheduleNotificationType getScheduleNotificationType() {
        return scheduleNotificationType;
    }

    public void setScheduleNotificationType(ScheduleNotificationType scheduleNotificationType) {
        this.scheduleNotificationType = scheduleNotificationType;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }
}
