package com.jtravan.scheduler;

import com.jtravan.model.Action;
import com.jtravan.model.Operation;
import com.jtravan.model.Resource;
import com.jtravan.model.ResourceCategoryDataStructure;
import com.jtravan.model.ResourceNotification;
import com.jtravan.model.ResourceOperation;
import com.jtravan.model.Transaction;
import com.jtravan.model.TransactionNotification;
import com.jtravan.model.TransactionNotificationType;
import com.jtravan.services.PredictionBasedSchedulerActionService;
import com.jtravan.services.PredictionBasedSchedulerActionServiceImpl;
import com.jtravan.services.ResourceNotificationHandler;
import com.jtravan.services.ResourceNotificationManager;
import com.jtravan.services.TransactionNotificationHandler;
import com.jtravan.services.TransactionNotificationManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by johnravan on 11/17/16.
 */
public class PredictionBasedScheduler implements TransactionExecutor,
        ResourceNotificationHandler, TransactionNotificationHandler, Runnable {

    private PredictionBasedSchedulerActionService predictionBasedSchedulerActionService;
    private ResourceCategoryDataStructure resourceCategoryDataStructure_READ;
    private ResourceCategoryDataStructure resourceCategoryDataStructure_WRITE;
    private ResourceNotificationManager resourceNotificationManager;
    private TransactionNotificationManager scheduleNotificationManager;
    private Map<Resource, Integer> resourcesWeHaveLockOn_Read;
    private Map<Resource, Integer> resourcesWeHaveLockOn_Write;
    private Resource resourceWaitingOn;
    private Transaction transaction;
    private String schedulerName;
    private CyclicBarrier gate;

    private long startTime;
    private long endTime;

    private boolean isAborted;

    public PredictionBasedScheduler(Transaction transaction, String name, boolean isSandBoxExecution) {
        constructorOperations(transaction, name, isSandBoxExecution);
    }

    private void constructorOperations(Transaction transaction, String name, boolean isSandBoxExecution) {
        this.transaction = transaction;
        this.schedulerName = name;
        this.resourcesWeHaveLockOn_Read = new HashMap<Resource, Integer>();
        this.resourcesWeHaveLockOn_Write = new HashMap<Resource, Integer>();

        isAborted = false;

        scheduleNotificationManager = TransactionNotificationManager.getInstance(isSandBoxExecution);
        scheduleNotificationManager.registerHandler(this);

        resourceNotificationManager = scheduleNotificationManager.getResourceNotificationManager();
        resourceNotificationManager.registerHandler(this);

        predictionBasedSchedulerActionService = PredictionBasedSchedulerActionServiceImpl.getInstance(scheduleNotificationManager);
        resourceCategoryDataStructure_READ = ResourceCategoryDataStructure.getReadInstance(isSandBoxExecution);
        resourceCategoryDataStructure_WRITE = ResourceCategoryDataStructure.getWriteInstance(isSandBoxExecution);
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public String getSchedulerName() {
        return schedulerName;
    }

    public void setGate(CyclicBarrier gate) { this.gate = gate; }

    private boolean growingPhaseSuccessful() {

        if (transaction == null) {
            return false;
        }

        startTime = System.currentTimeMillis();

        // two phase locking - growing phase
        System.out.println("=========================================================");
        System.out.println(schedulerName + ": Two-phase locking growing phase initiated.");
        System.out.println("=========================================================");
        for (ResourceOperation resourceOperation : transaction.getResourceOperationList()) {

            if(resourceOperation.isAbortOperation()) {
                isAborted = true;
                System.out.println(schedulerName + ": Execution aborted due to LCLE");
                return false;
            }

            if (isAborted) {
                return handleAbortOperation();
            }

            Action action = predictionBasedSchedulerActionService
                    .determineSchedulerAction(resourceCategoryDataStructure_READ,
                            resourceCategoryDataStructure_WRITE, resourceOperation);

            System.out.println(schedulerName + ": Action for resource " + resourceOperation.getResource() + ": " + action.name());

            switch (action) {
                case DECLINE:

                    if (resourceOperation.getOperation() == Operation.WRITE &&
                            resourcesWeHaveLockOn_Write.containsKey(resourceOperation.getResource())) {
                        System.out.println(schedulerName + ": Already have lock for Resource "
                                + resourceOperation.getResource() + ". Continuing execution");

                        Integer lockCount = resourcesWeHaveLockOn_Write.get(resourceOperation.getResource());
                        lockCount++;
                        insertIntoCorrectRCDS(resourceOperation);
                        resourcesWeHaveLockOn_Write.put(resourceOperation.getResource(), lockCount);
                        continue;
                    } else if(resourceOperation.getOperation() == Operation.READ &&
                            (resourcesWeHaveLockOn_Read.containsKey(resourceOperation.getResource()) ||
                                    resourcesWeHaveLockOn_Write.containsKey(resourceOperation.getResource() ))) {

                        System.out.println(schedulerName + ": Already have lock for Resource "
                                + resourceOperation.getResource() + ". Continuing execution");

                        Integer lockCount = resourcesWeHaveLockOn_Read.get(resourceOperation.getResource());
                        lockCount++;
                        insertIntoCorrectRCDS(resourceOperation);
                        resourcesWeHaveLockOn_Read.put(resourceOperation.getResource(), lockCount);
                        continue;
                    } else {
                        resourceWaitingOn = resourceOperation.getResource();
                        System.out.println(schedulerName + ": Waiting for lock on Resource "
                                + resourceOperation.getResource() + " to be released...");
                        try {
                            synchronized (this) {
                                wait();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        System.out.println(schedulerName + ": Lock for Resource " + resourceOperation.getResource()
                                + " released and obtained");

                        insertIntoCorrectRCDS(resourceOperation);
                        resourcesWeHaveLockOn_Read.put(resourceOperation.getResource(), 1);
                        resourceNotificationManager.lock(resourceOperation.getResource(), resourceOperation.getOperation());
                    }

                    break;
                case ELEVATE:

                    System.out.println(schedulerName + ": Other transaction abort initiated. Now locking resource...");
                    resourceWaitingOn = resourceOperation.getResource();
                    resourceNotificationManager.lock(resourceOperation.getResource(), resourceOperation.getOperation());
                    if(resourceOperation.getOperation() == Operation.READ) {
                        resourcesWeHaveLockOn_Read.put(resourceOperation.getResource(), 1);
                    } else {
                        resourcesWeHaveLockOn_Write.put(resourceOperation.getResource(), 1);
                    }

                    break;
                case GRANT:

                    insertIntoCorrectRCDS(resourceOperation);

                    if(resourceOperation.getOperation() == Operation.WRITE &&
                            resourcesWeHaveLockOn_Write.containsKey(resourceOperation.getResource())) {
                        System.out.println(schedulerName + ": Already have lock for Resource "
                                + resourceOperation.getResource() + ". Continuing execution");

                        Integer lockCount = resourcesWeHaveLockOn_Write.get(resourceOperation.getResource());
                        lockCount++;
                        resourcesWeHaveLockOn_Write.put(resourceOperation.getResource(), lockCount);
                        continue;
                    } else if(resourceOperation.getOperation() == Operation.READ &&
                            resourcesWeHaveLockOn_Read.containsKey(resourceOperation.getResource())) {
                        System.out.println(schedulerName + ": Already have lock for Resource "
                                + resourceOperation.getResource() + ". Continuing execution");

                        Integer lockCount = resourcesWeHaveLockOn_Read.get(resourceOperation.getResource());
                        lockCount++;
                        resourcesWeHaveLockOn_Read.put(resourceOperation.getResource(), lockCount);
                        continue;
                    } else {
                        System.out.println(schedulerName + ": No lock obtained for Resource " + resourceOperation.getResource() + ". Locking now...");

                        if (resourceOperation.getOperation() == Operation.READ) {
                            resourcesWeHaveLockOn_Read.put(resourceOperation.getResource(), 1);
                        } else {
                            resourcesWeHaveLockOn_Write.put(resourceOperation.getResource(), 1);
                        }

                        resourceNotificationManager.lock(resourceOperation.getResource(), resourceOperation.getOperation());
                    }

                    break;
                default:
                    throw new IllegalArgumentException("Case not handled.");
            }

        }

        return true;

    }

    private boolean shrinkingPhaseSuccessful() {

        // two phase locking - shrinking phase
        System.out.println("==========================================================");
        System.out.println(schedulerName + ": Two-phase locking shrinking phase initiated");
        System.out.println("==========================================================");
        for (ResourceOperation resourceOperation : transaction.getResourceOperationList()) {

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

            Integer lockCount;
            if(resourceOperation.getOperation() == Operation.READ) {
                lockCount = resourcesWeHaveLockOn_Read.get(resourceOperation.getResource());
            } else {
                lockCount = resourcesWeHaveLockOn_Write.get(resourceOperation.getResource());
            }

            if (lockCount != null && lockCount == 1) {
                if (resourceOperation.getOperation() == Operation.READ) {
                    resourcesWeHaveLockOn_Read.remove(resourceOperation.getResource());
                } else {
                    resourcesWeHaveLockOn_Write.remove(resourceOperation.getResource());
                }

                System.out.println(schedulerName + ": No longer needing the lock. Releasing lock...");
                removeFromCorrectRCDS(resourceOperation);
                resourceNotificationManager.unlock(resourceOperation.getResource());
            } else {
                if (lockCount == null) {
                    if(resourceOperation.getOperation() == Operation.READ) {
                        resourcesWeHaveLockOn_Read.put(resourceOperation.getResource(), 0);
                    } else {
                        resourcesWeHaveLockOn_Write.put(resourceOperation.getResource(), 0);
                    }
                } else {
                    System.out.println(schedulerName + ": Transaction still requires lock. Not unlocking just yet...");
                    lockCount--;
                    if(resourceOperation.getOperation() == Operation.READ) {
                        resourcesWeHaveLockOn_Read.put(resourceOperation.getResource(), lockCount);
                    } else {
                        resourcesWeHaveLockOn_Write.put(resourceOperation.getResource(), lockCount);
                    }

                }
            }

        }

        return true;

    }

    @SuppressWarnings("Duplicates")
    public boolean executeTransaction() {

        if (!growingPhaseSuccessful()) {
            return false;
        }

        if (isAborted) {
            return handleAbortOperation();
        }

        if (!shrinkingPhaseSuccessful()) {
            return false;
        }

        System.out.println(schedulerName + ": has successfully completed execution!");
        endTime = System.currentTimeMillis();

        TransactionNotification transactionNotification = new TransactionNotification();
        transactionNotification.setTransaction(transaction);
        transactionNotification.setTransactionNotificationType(TransactionNotificationType.TRANSACTION_COMPLETE);
        scheduleNotificationManager.handleTransactionNotification(transactionNotification);

        scheduleNotificationManager.deregisterHandler(this);
        resourceNotificationManager.deregisterHandler(this);

        return true;
    }

    private void insertIntoCorrectRCDS(ResourceOperation resourceOperation) {

        if (resourceOperation.getOperation() == Operation.READ) {
            resourceCategoryDataStructure_READ.insertResourceOperationForResource(resourceOperation.getResource(), resourceOperation);
        } else {
            resourceCategoryDataStructure_WRITE.insertResourceOperationForResource(resourceOperation.getResource(), resourceOperation);
        }

    }

    private void removeFromCorrectRCDS(ResourceOperation resourceOperation) {

        if (resourceOperation.getOperation() == Operation.READ) {
            resourceCategoryDataStructure_READ.removeResourceOperationForResouce(resourceOperation.getResource(), resourceOperation);
        } else {
            resourceCategoryDataStructure_WRITE.removeResourceOperationForResouce(resourceOperation.getResource(), resourceOperation);
        }

    }

    public void run() {
        try {
            gate.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }

        boolean isFinished = executeTransaction();

        if (!isFinished) {
            System.out.println(getSchedulerName() + ": Aborted. Waiting for other transaction to finish before retrying execution");

//            synchronized (this) {
//                try {
//                    wait(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }

            // Clean up before re-running
            resourceNotificationManager.deregisterHandler(this);
            scheduleNotificationManager.deregisterHandler(this);

            for(ResourceOperation resourceOperation : transaction.getResourceOperationList()) {
                if(resourceOperation.getOperation() == Operation.WRITE) {
                    resourceCategoryDataStructure_WRITE.removeResourceOperationForResouce(resourceOperation.getResource(), resourceOperation);
                    resourceNotificationManager.unlock(resourceOperation.getResource());
                } else {
                    resourceCategoryDataStructure_READ.removeResourceOperationForResouce(resourceOperation.getResource(), resourceOperation);
                }
            }

            System.out.println(getSchedulerName() + ": Other transaction finished. Now retrying...");
            PredictionBasedScheduler predictionBasedScheduler_ForAbort = new PredictionBasedScheduler(getTransaction(),
                    "Sandbox scheduler for " + getSchedulerName(), true);
            boolean isSandboxExecutionSuccess = predictionBasedScheduler_ForAbort.executeTransaction();

            if(isSandboxExecutionSuccess) {
                System.out.println(predictionBasedScheduler_ForAbort.getSchedulerName() + ": Sandbox Execution Succeeded, Re-Run with Main System");

                constructorOperations(transaction, schedulerName, false);
                executeTransaction();
            } else {
                System.out.println(predictionBasedScheduler_ForAbort.getSchedulerName() + ": Sandbox Execution Failed. Execution Complete");
            }
        }
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

        if (!resourceNotification.isLocked()) {
            if (resourceNotification.getResource() == resourceWaitingOn) {
                System.out.println(schedulerName + ": Resource, " + resourceNotification.getResource()
                        + ", that we have been waiting on, has been released and unlocked ");
                synchronized (this) {
                    notifyAll();
                }
            }
        }
    }

    public void handleTransactionNotification(TransactionNotification transactionNotification) {

        if (transactionNotification == null) {
            return;
        }

        Transaction transaction = transactionNotification.getTransaction();
        TransactionNotificationType type = transactionNotification.getTransactionNotificationType();

        switch (type) {
            case ABORT:


                if(transaction == this.transaction) {
                    isAborted = true;
                    for (ResourceOperation ro : transaction.getResourceOperationList()) {
                        removeFromCorrectRCDS(ro);
                        resourceNotificationManager.unlock(ro.getResource());
                    }
                }

                break;
            case TRANSACTION_COMPLETE:

                if(transaction != this.transaction) {
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
