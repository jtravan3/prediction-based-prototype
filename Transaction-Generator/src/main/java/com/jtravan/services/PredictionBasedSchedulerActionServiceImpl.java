package com.jtravan.services;

import com.jtravan.model.*;

/**
 * Created by johnravan on 11/9/16.
 */
@SuppressWarnings("Duplicates")
public class PredictionBasedSchedulerActionServiceImpl implements PredictionBasedSchedulerActionService {

    private static PredictionBasedSchedulerActionServiceImpl theInstance;

    private PredictionBasedSchedulerActionServiceImpl(){}

    public synchronized static final PredictionBasedSchedulerActionServiceImpl getInstance() {

        if(theInstance == null) {
            theInstance = new PredictionBasedSchedulerActionServiceImpl();
        }
        return theInstance;

    }

    public synchronized Action determineSchedulerAction(ResourceCategoryDataStructure rcdsRead, ResourceCategoryDataStructure rcdsWrite, ResourceOperation resourceOperation) {

        Resource resource = resourceOperation.getResource();

        if (resourceOperation.getOperation() == Operation.WRITE) {

            if (rcdsWrite.getHighestPriorityForResource(resource) == null) {

                if (rcdsRead.getHighestPriorityForResource(resource) == null) {

                    rcdsWrite.insertResourceOperationForResource(resource, resourceOperation);
                    return Action.GRANT;

                } else if (Category.isCategory1HigherThanCategory2(resourceOperation.getAssociatedTransaction().getCategory(), rcdsRead.getHighestPriorityForResource(resource).getAssociatedTransaction().getCategory())) {

                    rcdsWrite.clearHeapForResource(resource);
                    rcdsWrite.insertResourceOperationForResource(resource, resourceOperation);
                    return Action.ELEVATE;

                } else { //rcdsRead is not empty

                    return Action.DECLINE;

                }

            } else { // rcdsWrite is not empty

                if (Category.isCategory1HigherThanOrEqualCategory2(rcdsWrite.getHighestPriorityForResource(resource).getAssociatedTransaction().getCategory(), resourceOperation.getAssociatedTransaction().getCategory())) {

                    return Action.DECLINE;

                } else {

                    if (rcdsRead.getHighestPriorityForResource(resource) == null) {

                        rcdsWrite.clearHeapForResource(resource);
                        rcdsWrite.insertResourceOperationForResource(resource, resourceOperation);
                        return Action.ELEVATE;

                    } else if (Category.isCategory1HigherThanCategory2(resourceOperation.getAssociatedTransaction().getCategory(), rcdsRead.getHighestPriorityForResource(resource).getAssociatedTransaction().getCategory())) {

                        rcdsWrite.clearHeapForResource(resource);
                        rcdsWrite.insertResourceOperationForResource(resource, resourceOperation);
                        return Action.ELEVATE;

                    } else {

                        return Action.DECLINE;

                    }

                }

            }

        } else { // operation is a read

            if (rcdsWrite.getHighestPriorityForResource(resource) == null) {

                rcdsRead.insertResourceOperationForResource(resource, resourceOperation);
                return Action.GRANT;

            } else {

                if (Category.isCategory1HigherThanOrEqualCategory2(rcdsWrite.getHighestPriorityForResource(resource).getAssociatedTransaction().getCategory(), resourceOperation.getAssociatedTransaction().getCategory())) {

                    return Action.DECLINE;

                } else {

                    rcdsRead.clearHeapForResource(resource);
                    rcdsRead.insertResourceOperationForResource(resource, resourceOperation);
                    return Action.ELEVATE;

                }

            }

        }

    }

}
