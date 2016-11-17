package com.jtravan.services;

import com.jtravan.model.Resource;
import com.jtravan.model.ResourceNotifcation;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by johnravan on 11/17/16.
 */
public class ResourceNotifcationManager implements  ResourceNotificationHandler{

    private static ResourceNotifcationManager theInstance;
    List<ResourceNotificationHandler> handlers;


    private ResourceNotifcationManager() {
        handlers = new LinkedList<ResourceNotificationHandler>();
    }

    public static final ResourceNotifcationManager getInstance() {

        if(theInstance == null) {
            theInstance = new ResourceNotifcationManager();
        }
        return theInstance;

    }

    public void lock(Resource resource) {
        resource.lock();

        ResourceNotifcation resourceNotifcation = new ResourceNotifcation();
        resourceNotifcation.setResource(resource);
        resourceNotifcation.setLocked(true);
        handleResourceNotification(resourceNotifcation);
    }

    public void unlock(Resource resource) {
        resource.unlock();

        ResourceNotifcation resourceNotifcation = new ResourceNotifcation();
        resourceNotifcation.setResource(resource);
        resourceNotifcation.setLocked(false);
        handleResourceNotification(resourceNotifcation);
    }

    public void registerHandler (ResourceNotificationHandler handler) {

        if (handler == null) {
            return;
        }

        handlers.add(handler);

    }

    public void deregisterHandler (ResourceNotificationHandler handler) {

        if (handler == null) {
            return;
        }

        handlers.remove(handler);

    }

    public void handleResourceNotification(ResourceNotifcation resourceNotifcation) {

        if (resourceNotifcation == null) {
            return;
        }

        for (ResourceNotificationHandler handler : handlers) {
            handler.handleResourceNotification(resourceNotifcation);
        }

    }
}
