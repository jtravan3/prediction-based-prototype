package com.jtravan.services;

import com.jtravan.model.Resource;
import com.jtravan.model.ResourceNotifcation;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by johnravan on 11/17/16.
 */
public class ResourceNotificationManager implements  ResourceNotificationHandler{

    private static ResourceNotificationManager theInstance;
    List<ResourceNotificationHandler> handlers;


    private ResourceNotificationManager() {
        handlers = new LinkedList<ResourceNotificationHandler>();
    }

    public synchronized static final ResourceNotificationManager getInstance() {

        if(theInstance == null) {
            theInstance = new ResourceNotificationManager();
        }
        return theInstance;

    }

    public synchronized void lock(Resource resource) {
        resource.lock();

        ResourceNotifcation resourceNotifcation = new ResourceNotifcation();
        resourceNotifcation.setResource(resource);
        resourceNotifcation.setLocked(true);
        System.out.println("Locking Resource " + resource);
        handleResourceNotification(resourceNotifcation);
    }

    public synchronized void unlock(Resource resource) {
        resource.unlock();

        ResourceNotifcation resourceNotifcation = new ResourceNotifcation();
        resourceNotifcation.setResource(resource);
        resourceNotifcation.setLocked(false);
        System.out.println("Unlocking Resource " + resource);
        handleResourceNotification(resourceNotifcation);
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

        System.out.println("Resource Notification Handler registered for notifications");
        handlers.remove(handler);

    }

    public synchronized void handleResourceNotification(ResourceNotifcation resourceNotifcation) {

        if (resourceNotifcation == null) {
            return;
        }

        for (ResourceNotificationHandler handler : handlers) {
            if (handler != null) {
                handler.handleResourceNotification(resourceNotifcation);
            }
        }

    }
}
