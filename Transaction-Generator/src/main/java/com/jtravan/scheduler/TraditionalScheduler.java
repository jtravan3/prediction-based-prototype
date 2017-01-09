package com.jtravan.scheduler;

import com.jtravan.model.Resource;
import com.jtravan.model.ResourceNotifcation;
import com.jtravan.model.ResourceOperation;
import com.jtravan.model.Schedule;
import com.jtravan.services.ResourceNotifcationManager;
import com.jtravan.services.ResourceNotificationHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by johnravan on 11/17/16.
 */
public class TraditionalScheduler implements ScheduleExecutor, ResourceNotificationHandler {

    private Map<Resource, Integer> resourcesWeHaveLockOn;
    private Resource resourceWaitingOn;
    private Schedule schedule;
    private String schedulerName;
    private ResourceNotifcationManager resourceNotifcationManager;

    public TraditionalScheduler(Schedule schedule, String name) {
        this.schedulerName = name;
        this.schedule = schedule;
        this.resourcesWeHaveLockOn = new HashMap<Resource, Integer>();
        resourceNotifcationManager = ResourceNotifcationManager.getInstance();
        resourceNotifcationManager.registerHandler(this);
    }

    public void executeSchedule() {

        if (schedule == null) {
            return;
        }

        // two phase locking - growing phase
        System.out.println("=========================================================");
        System.out.println(schedulerName + ": Two-phase locking growing phase initiated.");
        System.out.println("=========================================================");
        for (ResourceOperation resourceOperation : schedule.getResourceOperationList()) {

            if (resourceOperation.getResource().isLocked()) {

                System.out.println(schedulerName + ": Obtaining lock for Resource " + resourceOperation.getResource());
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
                    resourceNotifcationManager.lock(resourceOperation.getResource());

                }

            } else {

                System.out.println(schedulerName + ": No lock obtained for Resource " + resourceOperation.getResource());
                resourcesWeHaveLockOn.put(resourceOperation.getResource(), 1);
                resourceNotifcationManager.lock(resourceOperation.getResource());

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
                resourceNotifcationManager.unlock(resourceOperation.getResource());
            } else {
                resourcesWeHaveLockOn.put(resourceOperation.getResource(), --lockCount);
            }

        }

        System.out.println(schedulerName + ": has successfully completed execution!");

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
}
