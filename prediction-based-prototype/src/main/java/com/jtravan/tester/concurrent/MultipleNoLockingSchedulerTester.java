package com.jtravan.tester.concurrent;

import com.jtravan.com.jtravan.generator.TransactionGenerator;
import com.jtravan.model.ResourceOperation;
import com.jtravan.model.Transaction;
import com.jtravan.scheduler.NoLockingScheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultipleNoLockingSchedulerTester {

    private static final int NUM_OF_SCHEDULERS_EXECUTING = 2;
    private static final int NUM_OF_OPERATIONS_PER_TRANSACTION = 10;
    private static final int NUM_OF_TRANSACTIONS = 1;

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {

        List<NoLockingScheduler> noLockingSchedulerLinkedList = new LinkedList<NoLockingScheduler>();
        TransactionGenerator transactionGenerator = TransactionGenerator.getInstance();
        List<Transaction> transactionList = transactionGenerator.generateRandomTransactions(NUM_OF_OPERATIONS_PER_TRANSACTION, NUM_OF_TRANSACTIONS, true);

        int count = 0;
        for(Transaction transaction : transactionList) {
            count++;
            noLockingSchedulerLinkedList.add(new NoLockingScheduler(transaction, "Scheduler " + count, false));
        }

        // Total time if no overhead
        int totalWithoutOverhead = 0;
        for(NoLockingScheduler noLockingScheduler : noLockingSchedulerLinkedList) {
            for (ResourceOperation ro : noLockingScheduler.getTransaction().getResourceOperationList()) {
                totalWithoutOverhead += ro.getExecutionTime();
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(NUM_OF_SCHEDULERS_EXECUTING);
        CyclicBarrier gate = new CyclicBarrier(NUM_OF_SCHEDULERS_EXECUTING + 1);

        final long startTime = System.currentTimeMillis();

        for (NoLockingScheduler noLockingScheduler: noLockingSchedulerLinkedList) {
            noLockingScheduler.setGate(gate);
            executorService.execute(noLockingScheduler);
        }

        System.out.println("Starting now...");
        Thread.sleep(1000);

        gate.await();

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);

        final long endTime = System.currentTimeMillis();
        System.out.println("Total time without overhead: " + totalWithoutOverhead);
        System.out.println("Total execution time: " + (endTime - startTime));
    }

}
