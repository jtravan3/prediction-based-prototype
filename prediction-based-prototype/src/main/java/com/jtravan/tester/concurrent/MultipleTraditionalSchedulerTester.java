package com.jtravan.tester.concurrent;

import com.jtravan.com.jtravan.generator.ScheduleGenerator;
import com.jtravan.com.jtravan.generator.TransactionGenerator;
import com.jtravan.model.ResourceOperation;
import com.jtravan.model.Schedule;
import com.jtravan.model.Transaction;
import com.jtravan.scheduler.TraditionalScheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by johnravan on 1/7/17.
 */
public class MultipleTraditionalSchedulerTester {

    private static final int NUM_OF_SCHEDULERS_EXECUTING = 2;
    private static final int NUM_OF_OPERATIONS_PER_TRANSACTION = 12;
    private static final int NUM_OF_TRANSACTIONS = 2;

    public static void main(String[] args) throws InterruptedException {

        List<TraditionalScheduler> traditionalSchedulerList = new LinkedList<TraditionalScheduler>();

        for(int i = 0; i < NUM_OF_SCHEDULERS_EXECUTING; i++) {
            TransactionGenerator transactionGenerator = TransactionGenerator.getInstance();
            List<Transaction> transactionList = transactionGenerator.generateRandomTransactions(NUM_OF_OPERATIONS_PER_TRANSACTION, NUM_OF_TRANSACTIONS, false);

            ScheduleGenerator scheduleGenerator = ScheduleGenerator.getInstance();
//            Schedule schedule;
//            if (i == 0) {
//                schedule = scheduleGenerator.create1of2ExampleSchedule_NonConflicting(Category.LCLE);
//            } else {
//                schedule = scheduleGenerator.create2of2ExampleSchedule_NonConflicting(Category.LCHE);
//            }

            Schedule schedule = scheduleGenerator.createSchedule(transactionList);
            System.out.println("Schedule to be executed: " + schedule);

            traditionalSchedulerList.add(new TraditionalScheduler(schedule, "Scheduler " + i));
        }

        // Total time if no overhead
        int totalWithoutOverhead = 0;
        for(TraditionalScheduler ts : traditionalSchedulerList) {
            for (ResourceOperation ro : ts.getSchedule().getResourceOperationList()) {
                totalWithoutOverhead += ro.getExecutionTime();
            }
        }

        ExecutorService executorService = Executors.newCachedThreadPool();

        final long startTime = System.currentTimeMillis();

        for (TraditionalScheduler traditionalScheduler: traditionalSchedulerList) {
            executorService.execute(traditionalScheduler);
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);

        final long endTime = System.currentTimeMillis();
        System.out.println("Total time without overhead: " + totalWithoutOverhead);
        System.out.println("Total execution time: " + (endTime - startTime));
    }
}
