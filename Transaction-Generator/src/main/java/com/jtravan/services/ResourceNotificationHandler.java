package com.jtravan.services;

import com.jtravan.model.ResourceNotifcation;

/**
 * Created by johnravan on 11/17/16.
 */
public interface ResourceNotificationHandler {
    void handleResourceNotification(ResourceNotifcation resourceNotifcation);
}
