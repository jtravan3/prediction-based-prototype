package com.jtravan.scheduler;

import com.jtravan.model.Action;
import com.jtravan.model.ResourceCategoryDataStructure;
import com.jtravan.model.ResourceOperation;
import com.jtravan.model.Schedule;
import com.jtravan.services.PredictionBasedSchedulerActionService;
import com.jtravan.services.PredictionBasedSchedulerActionServiceImpl;

/**
 * Created by johnravan on 11/17/16.
 */
public class PredictionBasedScheduler implements ScheduleExecutor {

    private PredictionBasedSchedulerActionService predictionBasedSchedulerActionService;
    private ResourceCategoryDataStructure resourceCategoryDataStructure_READ;
    private ResourceCategoryDataStructure resourceCategoryDataStructure_WRITE;

    public PredictionBasedScheduler() {

        predictionBasedSchedulerActionService = new PredictionBasedSchedulerActionServiceImpl();
        resourceCategoryDataStructure_READ = ResourceCategoryDataStructure.getReadInstance();
        resourceCategoryDataStructure_WRITE = ResourceCategoryDataStructure.getWriteInstance();

    }

    public void executeSchedule (Schedule schedule) {

        if (schedule == null) {
            return;
        }

        for (ResourceOperation resourceOperation : schedule.getResourceOperationList()) {

            Action action = predictionBasedSchedulerActionService.determineSchedulerAction(resourceCategoryDataStructure_READ, resourceCategoryDataStructure_WRITE, resourceOperation);

            if (action == Action.DECLINE) {

                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(resourceOperation.getExecutionTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } else if (action == Action.ELEVATE) {

            } else if (action == Action.GRANT) {

            } else {
                throw new IllegalStateException("Should never hit this case. There are only 3 action types");
            }

        }

    }
}
