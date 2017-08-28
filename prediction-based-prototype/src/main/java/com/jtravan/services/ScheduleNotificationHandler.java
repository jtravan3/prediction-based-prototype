package com.jtravan.services;

import com.jtravan.model.ScheduleNotification;

/**
 * Created by johnravan on 1/11/17.
 */
public interface ScheduleNotificationHandler {
    void handleScheduleNotification(ScheduleNotification scheduleNotification);
}
