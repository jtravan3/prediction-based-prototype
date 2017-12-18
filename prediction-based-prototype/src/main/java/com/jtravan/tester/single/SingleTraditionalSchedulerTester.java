package com.jtravan.tester.single;

import com.jtravan.com.jtravan.generator.TransactionGenerator;
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
        List<Transaction> transactionList = transactionGenerator.generateRandomTransactions(NUM_OF_OPERATIONS_PER_TRANSACTION, NUM_OF_TRANSACTIONS, false);

        System.out.println("Schedule to be executed: " + transactionList.get(0));

        final long startTime = System.currentTimeMillis();
        TraditionalScheduler traditionalScheduler = new TraditionalScheduler(transactionList.get(0), "Scheduler 1");
        traditionalScheduler.executeTransaction();
        final long endTime = System.currentTimeMillis();

        System.out.println("Total execution time: " + (endTime - startTime));
    }
}
