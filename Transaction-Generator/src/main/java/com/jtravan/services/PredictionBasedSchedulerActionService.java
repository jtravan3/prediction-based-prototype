package com.jtravan.services;

import com.jtravan.model.Action;
import com.jtravan.model.ResourceCategoryDataStructure;
import com.jtravan.model.ResourceOperation;

/**
 * Created by johnravan on 11/9/16.
 */
public interface PredictionBasedSchedulerActionService {
    Action determineSchedulerAction(ResourceCategoryDataStructure rcdsRead, ResourceCategoryDataStructure rcdsWrite, ResourceOperation resourceOperation);
}
