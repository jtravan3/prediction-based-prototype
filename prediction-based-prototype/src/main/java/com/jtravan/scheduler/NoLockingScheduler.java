package com.jtravan.scheduler;

import com.jtravan.model.*;
import com.jtravan.services.ResourceNotificationHandler;
import com.jtravan.services.ResourceNotificationManager;
import com.jtravan.services.ScheduleNotificationHandler;
import com.jtravan.services.ScheduleNotificationManager;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class NoLockingScheduler implements ScheduleExecutor,
        ResourceNotificationHandler, ScheduleNotificationHandler, Runnable  {

    private ResourceNotificationManager resourceNotificationManager;
    private ScheduleNotificationManager scheduleNotificationManager;
    private Schedule schedule;
    private String schedulerName;
    private CyclicBarrier gate;

    private long startTime;
    private long endTime;

    private boolean isAborted;

    public NoLockingScheduler(Schedule schedule, String name, boolean isSandBoxExecution) {
        constructorOperations(schedule, name, isSandBoxExecution);
    }

    private void constructorOperations(Schedule schedule, String name, boolean isSandBoxExecution) {

        this.schedule = schedule;
        this.schedulerName = name;

        isAborted = false;

        scheduleNotificationManager = ScheduleNotificationManager.getInstance(isSandBoxExecution);
        scheduleNotificationManager.registerHandler(this);

        resourceNotificationManager = scheduleNotificationManager.getResourceNotificationManager();
        resourceNotificationManager.registerHandler(this);

    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setGate(CyclicBarrier gate) { this.gate = gate; }

    @SuppressWarnings("Duplicates")
    public boolean executeSchedule() {

        if (schedule == null) {
            return false;
        }

        startTime = System.currentTimeMillis();

        // two phase locking - growing phase
        System.out.println("=========================================================");
        System.out.println(schedulerName + ": Two-phase locking growing phase initiated.");
        System.out.println("=========================================================");

        if (isAborted) {
            return handleAbortOperation();
        }

        // two phase locking - shrinking phase
        System.out.println("==========================================================");
        System.out.println(schedulerName + ": Two-phase locking shrinking phase initiated");
        System.out.println("==========================================================");
        for (ResourceOperation resourceOperation : schedule.getResourceOperationList()) {

            if (isAborted) {
                return handleAbortOperation();
            }

            try {
                System.out.println(schedulerName + ": Executing operation on Resource " + resourceOperation.getResource());
                Thread.sleep(resourceOperation.getExecutionTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (isAborted) {
                return handleAbortOperation();
            }

        }

        System.out.println(schedulerName + ": has successfully completed execution!");
        endTime = System.currentTimeMillis();

        ScheduleNotification scheduleNotification = new ScheduleNotification();
        scheduleNotification.setSchedule(schedule);
        scheduleNotification.setScheduleNotificationType(ScheduleNotificationType.SCHEDULE_COMPLETE);
        scheduleNotificationManager.handleScheduleNotification(scheduleNotification);

        scheduleNotificationManager.deregisterHandler(this);
        resourceNotificationManager.deregisterHandler(this);

        return true;
    }

    public void run() {
        try {
            gate.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        executeSchedule();

    }

    private boolean handleAbortOperation() {
        System.out.println(schedulerName + ": Execution aborted");
        System.out.println(schedulerName + ": Waiting and trying execution again");
        return false;
    }

    @SuppressWarnings("Duplicates")
    public void handleResourceNotification(ResourceNotification resourceNotification) {

        if (resourceNotification == null) {
            return;
        }

    }

    public void handleScheduleNotification(ScheduleNotification scheduleNotification) {

        if (scheduleNotification == null) {
            return;
        }

        Schedule schedule = scheduleNotification.getSchedule();
        ScheduleNotificationType type = scheduleNotification.getScheduleNotificationType();

        switch (type) {
            case ABORT:
            case SCHEDULE_COMPLETE:

                if(schedule != this.schedule) {
                    // Notify any waiting
                    synchronized (this) {
                        System.out.println(schedulerName + ": Notifying just in case we need to start re-run");
                        notifyAll();
                    }
                }

                break;
            default:
                throw new IllegalStateException("Case not handled yet");
        }
    }

}
