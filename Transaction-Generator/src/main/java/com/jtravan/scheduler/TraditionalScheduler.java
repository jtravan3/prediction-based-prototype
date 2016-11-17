package com.jtravan.scheduler;

import com.jtravan.model.Resource;
import com.jtravan.model.ResourceNotifcation;
import com.jtravan.model.ResourceOperation;
import com.jtravan.model.Schedule;
import com.jtravan.services.ResourceNotifcationManager;
import com.jtravan.services.ResourceNotificationHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by johnravan on 11/17/16.
 */
public class TraditionalScheduler implements ScheduleExecutor, ResourceNotificationHandler {

    private List<Resource> resourcesWeHaveLockOn;
    private Resource resourceWaitingOn;

    public TraditionalScheduler() {
        resourcesWeHaveLockOn = new LinkedList<Resource>();
        ResourceNotifcationManager.getInstance().registerHandler(this);
    }

    public void executeSchedule(Schedule schedule) {

        if (schedule == null) {
            return;
        }

        // two phase locking - growing phase
        for (ResourceOperation resourceOperation : schedule.getResourceOperationList()) {

            if (resourceOperation.getResource().isLocked()) {

                if(resourcesWeHaveLockOn.contains(resourceOperation.getResource())) {
                    continue;
                } else {
                    resourceWaitingOn = resourceOperation.getResource();
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    resourcesWeHaveLockOn.add(resourceOperation.getResource());
                    ResourceNotifcationManager.getInstance().lock(resourceOperation.getResource());

                }

            } else {

                resourcesWeHaveLockOn.add(resourceOperation.getResource());
                ResourceNotifcationManager.getInstance().lock(resourceOperation.getResource());

            }

        }

        // two phase locking - shrinking phase
        for (ResourceOperation resourceOperation : schedule.getResourceOperationList()) {

            try {
                Thread.sleep(resourceOperation.getExecutionTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            resourcesWeHaveLockOn.remove(resourceOperation.getResource());
            ResourceNotifcationManager.getInstance().unlock(resourceOperation.getResource());

        }

    }

    public void handleResourceNotification(ResourceNotifcation resourceNotifcation) {

        if (resourceNotifcation == null) {
            return;
        }

        if (!resourceNotifcation.isLocked()) {
            if (resourceNotifcation.getResource() == resourceWaitingOn) {
                notifyAll();
            }
        }

    }
}
