package com.jtravan.tester;

import com.jtravan.com.jtravan.generator.TransactionGenerator;
import com.jtravan.model.Transaction;

import java.util.List;

/**
 * Created by johnravan on 3/30/16.
 */
public class TransactionGeneratorTester {

    public static void main(String[] args) {

        TransactionGenerator generator = TransactionGenerator.getInstance();
        List<Transaction> transactions = generator.generateRandomTransactions(5, 10, false);

        for(Transaction transaction : transactions) {
            System.out.println(transaction.toString());
        }

    }

}
