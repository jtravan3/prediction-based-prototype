package com.jtravan.tester;

import com.jtravan.com.jtravan.generator.TransactionGenerator;
import com.jtravan.model.Schedule;
import com.jtravan.model.Transaction;
import com.jtravan.scheduler.Scheduler;

import java.util.List;

/**
 * Created by johnravan on 3/30/16.
 */
public class TransactionGeneratorTester {

    public static void main(String[] args) {
        TransactionGenerator generator = TransactionGenerator.getInstance();
        List<Transaction> transactions = generator.generateRandomTransactions(5, 10);

        for(Transaction transaction : transactions) {
            System.out.println(transaction.toString());
        }

//        Scheduler scheduler = Scheduler.getInstance();
//        Schedule schedule = scheduler.createSchedule(transactions);
//
//        System.out.println();
//        System.out.println(schedule.toString());

    }

}
