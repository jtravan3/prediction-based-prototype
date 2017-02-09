package com.jtravan.tester;

import com.jtravan.com.jtravan.generator.ScheduleGenerator;
import com.jtravan.model.Category;
import com.jtravan.model.Schedule;
import com.jtravan.scheduler.PredictionBasedScheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by johnravan on 1/26/17.
 */
public class MultiplePredictionBasedSchedulerTester {

    private static final int NUM_OF_SCHEDULERS_EXECUTING = 2;
    private static final int NUM_OF_OPERATIONS_PER_TRANSACTION = 20;
    private static final int NUM_OF_TRANSACTIONS = 1;

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {

        List<PredictionBasedScheduler> predictionBasedSchedulerList = new LinkedList<PredictionBasedScheduler>();

        for(int i = 0; i < NUM_OF_SCHEDULERS_EXECUTING; i++) {
//            TransactionGenerator transactionGenerator = TransactionGenerator.getInstance();
//            List<Transaction> transactionList = transactionGenerator.generateRandomTransactions(NUM_OF_OPERATIONS_PER_TRANSACTION, NUM_OF_TRANSACTIONS);

            ScheduleGenerator scheduleGenerator = ScheduleGenerator.getInstance();
            Schedule schedule;
            if (i == 0) {
                schedule = scheduleGenerator.create1of2ExampleSchedule(Category.HCHE);
            } else {
                schedule = scheduleGenerator.create2of2ExampleSchedule(Category.HCHE);
            }

//            Schedule schedule = scheduleGenerator.createSchedule(transactionList);
            System.out.println("Schedule to be executed: " + schedule);

            predictionBasedSchedulerList.add(new PredictionBasedScheduler(schedule, "Scheduler " + i));
        }

        ExecutorService executorService = Executors.newFixedThreadPool(NUM_OF_SCHEDULERS_EXECUTING);
        CyclicBarrier gate = new CyclicBarrier(NUM_OF_SCHEDULERS_EXECUTING + 1);

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
        System.out.println("Total execution time: " + (endTime - startTime));
    }

}
