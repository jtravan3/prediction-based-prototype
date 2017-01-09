package com.jtravan.tester;

import com.jtravan.com.jtravan.generator.ScheduleGenerator;
import com.jtravan.com.jtravan.generator.TransactionGenerator;
import com.jtravan.model.Schedule;
import com.jtravan.model.Transaction;
import com.jtravan.scheduler.TraditionalScheduler;

import java.util.List;

/**
 * Created by johnravan on 1/7/17.
 */
public class SingleTraditionalSchedulerTester {

    private static final int NUM_OF_OPERATIONS_PER_TRANSACTION = 30;
    private static final int NUM_OF_TRANSACTIONS = 30;

    public static void main(String[] args) {

        TransactionGenerator transactionGenerator = TransactionGenerator.getInstance();
        List<Transaction> transactionList = transactionGenerator.generateRandomTransactions(NUM_OF_OPERATIONS_PER_TRANSACTION, NUM_OF_TRANSACTIONS);

        ScheduleGenerator scheduleGenerator = ScheduleGenerator.getInstance();
        Schedule schedule = scheduleGenerator.createSchedule(transactionList);

        System.out.println("Schedule to be executed: " + schedule);

        final long startTime = System.currentTimeMillis();
        TraditionalScheduler traditionalScheduler = new TraditionalScheduler(schedule, "Scheduler 1");
        traditionalScheduler.executeSchedule();
        final long endTime = System.currentTimeMillis();

        System.out.println("Total execution time: " + (endTime - startTime));
    }
}
