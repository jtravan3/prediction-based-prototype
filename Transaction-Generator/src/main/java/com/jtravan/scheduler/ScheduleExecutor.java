package com.jtravan.scheduler;

import com.jtravan.model.Schedule;

/**
 * Created by johnravan on 11/17/16.
 */
public interface ScheduleExecutor {
    void executeSchedule(Schedule schedule);
}
