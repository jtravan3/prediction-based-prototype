package com.jtravan.tester;

import com.jtravan.com.jtravan.generator.ScheduleGenerator;
import com.jtravan.com.jtravan.generator.TransactionGenerator;
import com.jtravan.model.Schedule;
import com.jtravan.model.Transaction;
import com.jtravan.scheduler.PredictionBasedScheduler;

import java.util.List;

/**
 * Created by johnravan on 1/12/17.
 */
public class SinglePredictionBasedSchedulerTester {

    private static final int NUM_OF_OPERATIONS_PER_TRANSACTION = 20;
    private static final int NUM_OF_TRANSACTIONS = 1;

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) throws InterruptedException {

        TransactionGenerator transactionGenerator = TransactionGenerator.getInstance();
        List<Transaction> transactionList = transactionGenerator.generateRandomTransactions(NUM_OF_OPERATIONS_PER_TRANSACTION, NUM_OF_TRANSACTIONS);

        ScheduleGenerator scheduleGenerator = ScheduleGenerator.getInstance();
        Schedule schedule = scheduleGenerator.createSchedule(transactionList);

        System.out.println("Schedule to be executed: " + schedule);

        PredictionBasedScheduler predictionBasedScheduler = new PredictionBasedScheduler(schedule, "Prediction-Based Scheduler 1");
        predictionBasedScheduler.executeSchedule();

        System.out.println("Total execution time: " + (predictionBasedScheduler.getEndTime() - predictionBasedScheduler.getStartTime()));
    }

}
