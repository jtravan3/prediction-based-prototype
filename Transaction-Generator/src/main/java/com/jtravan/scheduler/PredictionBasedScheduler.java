package com.jtravan.scheduler;

import com.jtravan.model.*;
import com.jtravan.services.*;

import java.util.HashMap;
import java.util.Map;

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

    private long startTime;
    private long endTime;

    private boolean isAborted;
    private boolean needsToNotify;

    public PredictionBasedScheduler(Schedule schedule, String name) {
        this.schedule = schedule;
        this.schedulerName = name;
        this.resourcesWeHaveLockOn = new HashMap<Resource, Integer>();

        isAborted = false;
        needsToNotify = false;

        resourceNotificationManager = ResourceNotificationManager.getInstance();
        resourceNotificationManager.registerHandler(this);

        scheduleNotificationManager = ScheduleNotificationManager.getInstance();
        scheduleNotificationManager.registerHandler(this);

        predictionBasedSchedulerActionService = PredictionBasedSchedulerActionServiceImpl.getInstance();
        resourceCategoryDataStructure_READ = ResourceCategoryDataStructure.getReadInstance();
        resourceCategoryDataStructure_WRITE = ResourceCategoryDataStructure.getWriteInstance();
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    @SuppressWarnings("Duplicates")
    public boolean executeSchedule() {

        if (schedule == null) {
            return false;
        }

        startTime = System.currentTimeMillis();

        // two phase locking - growing phase
        System.out.println("=========================================================");
        System.out.println(schedulerName + ": Two-phase locking growing phase initiated.");
        System.out.println("=========================================================");
        for (ResourceOperation resourceOperation : schedule.getResourceOperationList()) {

            if (isAborted) {
                return handleAbortOperation();
            }

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
                                wait(15000);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        System.out.println(schedulerName + ": Lock for Resource " + resourceOperation.getResource()
                                + " released and obtained");

                        if (resourceOperation.getOperation() == Operation.READ) {
                            resourceCategoryDataStructure_READ.insertResourceOperationForResource(resourceOperation.getResource(), resourceOperation);
                        } else {
                            resourceCategoryDataStructure_WRITE.insertResourceOperationForResource(resourceOperation.getResource(), resourceOperation);
                        }

                        resourcesWeHaveLockOn.put(resourceOperation.getResource(), 1);
                        resourceNotificationManager.lock(resourceOperation.getResource(), resourceOperation.getOperation());
                    }

                    break;
                case ELEVATE:

                    System.out.println(schedulerName + ": Other schedule abort initiated. Now locking resource...");
                    resourceWaitingOn = resourceOperation.getResource();
                    resourceNotificationManager.lock(resourceOperation.getResource(), resourceOperation.getOperation());
                    resourcesWeHaveLockOn.put(resourceOperation.getResource(), 1);
                    needsToNotify = true;

                    break;
                case GRANT:

                    if(resourcesWeHaveLockOn.containsKey(resourceOperation.getResource())) {
                        System.out.println(schedulerName + ": Already have lock for Resource "
                                + resourceOperation.getResource() + ". Continuing execution");

                        Integer lockCount = resourcesWeHaveLockOn.get(resourceOperation.getResource());
                        resourcesWeHaveLockOn.put(resourceOperation.getResource(), ++lockCount);
                        continue;
                    } else {

                        if (resourceOperation.getOperation() == Operation.READ) {
                            resourceCategoryDataStructure_READ.insertResourceOperationForResource(resourceOperation.getResource(), resourceOperation);
                        } else {
                            resourceCategoryDataStructure_WRITE.insertResourceOperationForResource(resourceOperation.getResource(), resourceOperation);
                        }

                        System.out.println(schedulerName + ": No lock obtained for Resource " + resourceOperation.getResource() + ". Locking now...");
                        resourcesWeHaveLockOn.put(resourceOperation.getResource(), 1);
                        resourceNotificationManager.lock(resourceOperation.getResource(), resourceOperation.getOperation());
                    }

                    break;
                default:
                    throw new IllegalArgumentException("Case not handled.");
            }

        }

        if (isAborted) {
            return handleAbortOperation();
        }

        // two phase locking - shrinking phase
        System.out.println("==========================================================");
        System.out.println(schedulerName + ": Two-phase locking shrinking phase initiated");
        System.out.println("==========================================================");
        for (ResourceOperation resourceOperation : schedule.getResourceOperationList()) {

            if (isAborted) {
                return handleAbortOperation();
            }

            try {
                System.out.println(schedulerName + ": Executing operation on Resource " + resourceOperation.getResource());
                Thread.sleep(resourceOperation.getExecutionTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isAborted) {
                return handleAbortOperation();
            }

            Integer lockCount = resourcesWeHaveLockOn.get(resourceOperation.getResource());
            if (lockCount != null && lockCount == 1) {
                resourcesWeHaveLockOn.remove(resourceOperation.getResource());
                System.out.println(schedulerName + ": No longer needing the lock. Releasing lock...");

                if (resourceOperation.getOperation() == Operation.READ) {
                    resourceCategoryDataStructure_READ.removeResourceOperationForResouce(resourceOperation.getResource(), resourceOperation);
                } else {
                    resourceCategoryDataStructure_WRITE.removeResourceOperationForResouce(resourceOperation.getResource(), resourceOperation);
                }

                resourceNotificationManager.unlock(resourceOperation.getResource());
            } else {
                if (lockCount == null) {
                    resourcesWeHaveLockOn.put(resourceOperation.getResource(), 0);
                } else {
                    System.out.println(schedulerName + ": Transaction still requires lock. Not unlocking just yet...");
                    resourcesWeHaveLockOn.put(resourceOperation.getResource(), --lockCount);
                }
            }

        }

        System.out.println(schedulerName + ": has successfully completed execution!");
        endTime = System.currentTimeMillis();

        synchronized (this) {
            notifyAll();
        }

        scheduleNotificationManager.deregisterHandler(this);
        resourceNotificationManager.deregisterHandler(this);

        return true;
    }

    private boolean handleAbortOperation() {
        System.out.println(schedulerName + ": Execution aborted");
        System.out.println(schedulerName + ": Waiting and trying execution again");
        scheduleNotificationManager.deregisterHandler(this);
        resourceNotificationManager.deregisterHandler(this);
        return false;
    }

    public void handleResourceNotification(ResourceNotifcation resourceNotifcation) {

        if (resourceNotifcation == null) {
            return;
        }

        if (!resourceNotifcation.isLocked()) {
            if (resourceNotifcation.getResource() == resourceWaitingOn) {
                System.out.println(schedulerName + ": Resource, " + resourceNotifcation.getResource()
                        + ", that we have been waiting on, has been released and unlocked ");
                synchronized (this) {
                    notifyAll();
                }
            }
        }
    }

    public void handleScheduleNotification(ScheduleNotification scheduleNotification) {

        if (scheduleNotification == null) {
            return;
        }

        if(scheduleNotification.getScheduleNotificationType() == ScheduleNotificationType.ABORT) {
            Schedule schedule = scheduleNotification.getSchedule();
            if(schedule == this.schedule) {
                isAborted = true;
                for (ResourceOperation ro : schedule.getResourceOperationList()) {

                    if (ro.getOperation() == Operation.READ) {
                        resourceCategoryDataStructure_READ.removeResourceOperationForResouce(ro.getResource(), ro);
                    } else {
                        resourceCategoryDataStructure_WRITE.removeResourceOperationForResouce(ro.getResource(), ro);
                    }

                    resourceNotificationManager.unlock(ro.getResource());
                }
            }
        }
    }
}
