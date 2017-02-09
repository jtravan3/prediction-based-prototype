package com.jtravan.services;

import com.jtravan.model.Operation;
import com.jtravan.model.Resource;
import com.jtravan.model.ResourceNotification;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by johnravan on 11/17/16.
 */
public class ResourceNotificationManager implements ResourceNotificationHandler{

    private static ResourceNotificationManager theInstance;
    List<ResourceNotificationHandler> handlers;


    private ResourceNotificationManager() {
        handlers = new LinkedList<ResourceNotificationHandler>();
    }

    public static final ResourceNotificationManager getInstance() {

        if(theInstance == null) {
            theInstance = new ResourceNotificationManager();
        }
        return theInstance;

    }

    @SuppressWarnings("Duplicates")
    public synchronized void lock(Resource resource, Operation operation) {

        if (operation == Operation.READ) {
            resource.lock();

            ResourceNotification resourceNotification = new ResourceNotification();
            resourceNotification.setResource(resource);
            resourceNotification.setLocked(true);
            System.out.println("Locking Resource " + resource);
            handleResourceNotification(resourceNotification);
        } else {
            if (!resource.isLocked()) {
                resource.lock();

                ResourceNotification resourceNotification = new ResourceNotification();
                resourceNotification.setResource(resource);
                resourceNotification.setLocked(true);
                System.out.println("Locking Resource " + resource);
                handleResourceNotification(resourceNotification);
            } else {
                throw new IllegalStateException("Cannot lock already locked resource that has a Write lock. Resource " + resource.name());
            }
        }
    }

    public void unlock(Resource resource) {
        if (resource.isLocked()) {
            resource.unlock();

            ResourceNotification resourceNotification = new ResourceNotification();
            resourceNotification.setResource(resource);
            resourceNotification.setLocked(false);
            System.out.println("Unlocking Resource " + resource);
            handleResourceNotification(resourceNotification);
        }
    }

    public boolean isAllLocksReleased() {

        boolean isAllLocksReleased = true;
        for(Resource resource : Resource.values()) {
            if (resource.isLocked()) {
                isAllLocksReleased = false;
                break;
            }
        }

        return  isAllLocksReleased;
    }

    public void resetAllLocks() {

        for(Resource resource : Resource.values()) {
            resource.unlock();
        }

    }

    public synchronized void registerHandler (ResourceNotificationHandler handler) {

        if (handler == null) {
            return;
        }

        System.out.println("Resource Notification Handler registered for notifications");
        handlers.add(handler);

    }

    public synchronized void deregisterHandler (ResourceNotificationHandler handler) {

        if (handler == null) {
            return;
        }

        System.out.println("Resource Notification Handler deregistered for notifications");
        handlers.remove(handler);

    }

    public synchronized void handleResourceNotification(ResourceNotification resourceNotification) {

        if (resourceNotification == null) {
            return;
        }

        for (ResourceNotificationHandler handler : handlers) {
            if (handler != null) {
                handler.handleResourceNotification(resourceNotification);
            }
        }

    }
}
