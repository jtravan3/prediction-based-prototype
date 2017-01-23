package com.jtravan.scheduler;

import com.jtravan.model.*;
import com.jtravan.services.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by johnravan on 11/17/16.
 */
public class PredictionBasedScheduler implements ScheduleExecutor,
        ResourceNotificationHandler, ScheduleNotificationHandler {

    private PredictionBasedSchedulerActionService predictionBasedSchedulerActionService;
    private ResourceCategoryDataStructure resourceCategoryDataStructure_READ;
    private ResourceCategoryDataStructure resourceCategoryDataStructure_WRITE;
    private ResourceNotificationManager resourceNotificationManager;
    private ScheduleNotificationManager scheduleNotificationManager;
    private Map<Resource, Integer> resourcesWeHaveLockOn;
    private Resource resourceWaitingOn;
    private Schedule schedule;
    private String schedulerName;
    private Future future;

    private long startTime;
    private long endTime;

    public PredictionBasedScheduler(Schedule schedule, String name) {
        this.schedule = schedule;
        this.schedulerName = name;
        this.resourcesWeHaveLockOn = new HashMap<Resource, Integer>();

        resourceNotificationManager = ResourceNotificationManager.getInstance();
        resourceNotificationManager.registerHandler(this);

        scheduleNotificationManager = ScheduleNotificationManager.getInstance();
        scheduleNotificationManager.registerHandler(this);

        predictionBasedSchedulerActionService = PredictionBasedSchedulerActionServiceImpl.getInstance();
        resourceCategoryDataStructure_READ = ResourceCategoryDataStructure.getReadInstance();
        resourceCategoryDataStructure_WRITE = ResourceCategoryDataStructure.getWriteInstance();
    }

    public synchronized void setFuture(Future future) {
        this.future = future;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    @SuppressWarnings("Duplicates")
    public void executeSchedule() {

        if (schedule == null) {
            return;
        }

        startTime = System.currentTimeMillis();

        // two phase locking - growing phase
        System.out.println("=========================================================");
        System.out.println(schedulerName + ": Two-phase locking growing phase initiated.");
        System.out.println("=========================================================");
        for (ResourceOperation resourceOperation : schedule.getResourceOperationList()) {

            Action action = predictionBasedSchedulerActionService
                    .determineSchedulerAction(resourceCategoryDataStructure_READ,
                            resourceCategoryDataStructure_WRITE, resourceOperation);

            System.out.println(schedulerName + ": Action for resource " + resourceOperation.getResource() + ": " + action.name());

            switch (action) {
                case DECLINE:

                    if(resourcesWeHaveLockOn.containsKey(resourceOperation.getResource())) {
                        System.out.println(schedulerName + ": Already have lock for Resource "
                                + resourceOperation.getResource() + ". Continuing execution");

                        Integer lockCount = resourcesWeHaveLockOn.get(resourceOperation.getResource());
                        resourcesWeHaveLockOn.put(resourceOperation.getResource(), ++lockCount);
                        continue;
                    } else {
                        resourceWaitingOn = resourceOperation.getResource();
                        System.out.println(schedulerName + ": Waiting for lock on Resource "
                                + resourceOperation.getResource() + " to be released...");
                        try {
                            synchronized (this) {
                                wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        System.out.println(schedulerName + ": Lock for Resource " + resourceOperation.getResource()
                                + " released and obtained");
                        resourcesWeHaveLockOn.put(resourceOperation.getResource(), 1);
                        resourceNotificationManager.lock(resourceOperation.getResource());

                    }

                    break;
                case ELEVATE:

                    System.out.println(schedulerName + ": Aborting schedule. Waiting for lock to be released...");
                    resourceWaitingOn = resourceOperation.getResource();
                    scheduleNotificationManager.abortSchedule(resourceOperation
                            .getAssociatedTransaction().getScheduleTransactionIsApartOf());

                    try {
                        synchronized (this) {
                            wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println(schedulerName + ": Lock released! Locking resource...");
                    resourceNotificationManager.lock(resourceOperation.getResource());
                    resourcesWeHaveLockOn.put(resourceOperation.getResource(), 1);

                    break;
                case GRANT:

                    if(resourcesWeHaveLockOn.containsKey(resourceOperation.getResource())) {
                        System.out.println(schedulerName + ": Already have lock for Resource "
                                + resourceOperation.getResource() + ". Continuing execution");

                        Integer lockCount = resourcesWeHaveLockOn.get(resourceOperation.getResource());
                        resourcesWeHaveLockOn.put(resourceOperation.getResource(), ++lockCount);
                        continue;
                    } else {
                        System.out.println(schedulerName + ": No lock obtained for Resource " + resourceOperation.getResource());
                        resourcesWeHaveLockOn.put(resourceOperation.getResource(), 1);
                        resourceNotificationManager.lock(resourceOperation.getResource());
                    }

                    break;
                default:
                    throw new IllegalArgumentException("Case not handled.");
            }

        }

        // two phase locking - shrinking phase
        System.out.println("==========================================================");
        System.out.println(schedulerName + ": Two-phase locking shrinking phase initiated");
        System.out.println("==========================================================");
        for (ResourceOperation resourceOperation : schedule.getResourceOperationList()) {

            try {
                System.out.println(schedulerName + ": Executing operation on Resource " + resourceOperation.getResource());
                Thread.sleep(resourceOperation.getExecutionTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Integer lockCount = resourcesWeHaveLockOn.get(resourceOperation.getResource());
            if (lockCount == 1) {
                resourcesWeHaveLockOn.remove(resourceOperation.getResource());
                System.out.println(schedulerName + ": No longer needing the lock. Releasing lock...");
                resourceNotificationManager.unlock(resourceOperation.getResource());
            } else {
                resourcesWeHaveLockOn.put(resourceOperation.getResource(), --lockCount);
                System.out.println(schedulerName + ": Transaction still requires lock. Not unlocking just yet...");
            }

        }

        System.out.println(schedulerName + ": has successfully completed execution!");
        endTime = System.currentTimeMillis();

    }

    public synchronized void handleResourceNotification(ResourceNotifcation resourceNotifcation) {

        if (resourceNotifcation == null) {
            return;
        }

        if (!resourceNotifcation.isLocked()) {
            if (resourceNotifcation.getResource() == resourceWaitingOn) {
                System.out.println(schedulerName + ": Resource, " + resourceNotifcation.getResource()
                        + ", that we have been waiting on, has been released and unlocked ");
                notifyAll();
            }
        }
    }

    public synchronized void handleScheduleNotification(ScheduleNotification scheduleNotification) {

        if (scheduleNotification == null) {
            return;
        }

        if(scheduleNotification.getScheduleNotificationType() == ScheduleNotificationType.ABORT) {
            Schedule schedule = scheduleNotification.getSchedule();
            if(schedule == this.schedule) {
                System.out.println(schedulerName + ": Schedule Aborted. Stopping execution and releasing locks...");
                this.future.cancel(true);
                for (ResourceOperation resourceOperation : schedule.getResourceOperationList()) {
                    resourceNotificationManager.unlock(resourceOperation.getResource());
                }
            }
        }

    }
}
