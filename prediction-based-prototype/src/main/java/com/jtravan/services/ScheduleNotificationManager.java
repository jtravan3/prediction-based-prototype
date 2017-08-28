package com.jtravan.services;

import com.jtravan.model.Schedule;
import com.jtravan.model.ScheduleNotification;
import com.jtravan.model.ScheduleNotificationType;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by johnravan on 1/11/17.
 */
public class ScheduleNotificationManager implements ScheduleNotificationHandler {

    private ResourceNotificationManager resourceNotificationManager;

    private List<ScheduleNotificationHandler> handlers;
    private static ScheduleNotificationManager theInstance;

    private ScheduleNotificationManager(boolean createOneTimeInstance) {
        handlers = new LinkedList<ScheduleNotificationHandler>();
        resourceNotificationManager = ResourceNotificationManager.getInstance(createOneTimeInstance);
    }

    public synchronized static final ScheduleNotificationManager getInstance(boolean createOneTimeInstance) {

        if(createOneTimeInstance) {
            return new ScheduleNotificationManager(createOneTimeInstance);
        } else {
            if(theInstance == null) {
                theInstance = new ScheduleNotificationManager(createOneTimeInstance);
            }
            return theInstance;
        }

    }

    public ResourceNotificationManager getResourceNotificationManager() {
        return resourceNotificationManager;
    }

    public void abortSchedule(Schedule schedule) {

        if (schedule == null) {
            return;
        }

        ScheduleNotification scheduleNotification = new ScheduleNotification();
        scheduleNotification.setSchedule(schedule);
        scheduleNotification.setScheduleNotificationType(ScheduleNotificationType.ABORT);
        handleScheduleNotification(scheduleNotification);

    }

    public void registerHandler (ScheduleNotificationHandler handler) {

        if (handler == null) {
            return;
        }

        System.out.println("Transaction Notification Handler registered for notifications");
        handlers.add(handler);

    }

    public void deregisterHandler (ScheduleNotificationHandler handler) {

        if (handler == null) {
            return;
        }

        System.out.println("Transaction Notification Handler deregistered for notifications");
        handlers.remove(handler);

    }

    public void handleScheduleNotification(ScheduleNotification scheduleNotification) {

        if (scheduleNotification == null) {
            return;
        }

        for (ScheduleNotificationHandler handler : handlers) {
            handler.handleScheduleNotification(scheduleNotification);
        }

    }

}
