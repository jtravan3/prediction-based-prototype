package com.jtravan.tester;

import com.jtravan.com.jtravan.generator.TransactionGenerator;
import com.jtravan.model.*;
import com.jtravan.scheduler.Scheduler;
import java.util.List;

/**
 * Created by johnravan on 5/9/16.
 */
public class ScheduleGeneratorTester {

    public static void main(String[] args) {

        TransactionGenerator generator = TransactionGenerator.getInstance();
        List<Transaction> transactions = generator.generateRandomTransactions(5, 10);

        for(Transaction transaction : transactions) {
            System.out.println(transaction.toString());
        }

        Transaction t1 = new Transaction();
        Transaction t2 = new Transaction();

        // T1 Operations
        ResourceOperation ra1 = new ResourceOperation();
        ra1.setOperation(Operation.READ);
        ra1.setResource(Resource.A);
        ra1.setAssociatedTransaction(t1);
        t1.addResourceOperation(ra1);

        ResourceOperation wa1 = new ResourceOperation();
        wa1.setOperation(Operation.WRITE);
        wa1.setResource(Resource.A);
        wa1.setAssociatedTransaction(t1);
        t1.addResourceOperation(wa1);

        ResourceOperation rb1 = new ResourceOperation();
        rb1.setOperation(Operation.READ);
        rb1.setResource(Resource.B);
        rb1.setAssociatedTransaction(t1);
        t1.addResourceOperation(rb1);

        ResourceOperation wb1 = new ResourceOperation();
        wb1.setOperation(Operation.WRITE);
        wb1.setResource(Resource.B);
        wb1.setAssociatedTransaction(t1);
        t1.addResourceOperation(wb1);

        // T2 Operations
        ResourceOperation ra2 = new ResourceOperation();
        ra2.setOperation(Operation.READ);
        ra2.setResource(Resource.A);
        ra2.setAssociatedTransaction(t1);
        t2.addResourceOperation(ra2);

        ResourceOperation wa2 = new ResourceOperation();
        wa2.setOperation(Operation.WRITE);
        wa2.setResource(Resource.A);
        wa2.setAssociatedTransaction(t1);
        t2.addResourceOperation(wa2);

        ResourceOperation rb2 = new ResourceOperation();
        rb2.setOperation(Operation.READ);
        rb2.setResource(Resource.B);
        rb2.setAssociatedTransaction(t1);
        t2.addResourceOperation(rb2);

        ResourceOperation wb2 = new ResourceOperation();
        wb2.setOperation(Operation.WRITE);
        wb2.setResource(Resource.B);
        wb2.setAssociatedTransaction(t1);
        t2.addResourceOperation(wb2);

        transactions.add(t1);
        transactions.add(t2);

        Scheduler scheduler = Scheduler.getInstance();
        Schedule schedule = scheduler.createSchedule(transactions);

        System.out.println();
        System.out.println(schedule.toString());

    }

}
