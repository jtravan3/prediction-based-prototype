package com.jtravan.tester.concurrent;

import com.jtravan.com.jtravan.generator.TransactionGenerator;
import com.jtravan.model.ResourceOperation;
import com.jtravan.model.Transaction;
import com.jtravan.scheduler.PredictionBasedScheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by johnravan on 1/26/17.
 */
public class MultiplePredictionBasedSchedulerTester {

    private static final int NUM_OF_OPERATIONS_PER_TRANSACTION = 10;
    private static final int NUM_OF_TRANSACTIONS = 100;

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {

        TransactionGenerator transactionGenerator = TransactionGenerator.getInstance();
        List<Transaction> transactionList = transactionGenerator.generateRandomTransactions(NUM_OF_OPERATIONS_PER_TRANSACTION, NUM_OF_TRANSACTIONS, true);
        List<PredictionBasedScheduler> predictionBasedSchedulerList = new LinkedList<PredictionBasedScheduler>();

        int count = 0;
        for(Transaction transaction : transactionList) {
            count++;
            predictionBasedSchedulerList.add(new PredictionBasedScheduler(transaction, "Scheduler " + count, false));
        }

        // Total time if no overhead
        int totalWithoutOverhead = 0;
        for(PredictionBasedScheduler pbs : predictionBasedSchedulerList) {
            for (ResourceOperation ro : pbs.getTransaction().getResourceOperationList()) {
                totalWithoutOverhead += ro.getExecutionTime();
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(transactionList.size());
        CyclicBarrier gate = new CyclicBarrier(transactionList.size() + 1);

        final long startTime = System.currentTimeMillis();

        for (PredictionBasedScheduler predictionBasedScheduler: predictionBasedSchedulerList) {
            predictionBasedScheduler.setGate(gate);
            executorService.execute(predictionBasedScheduler);
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
