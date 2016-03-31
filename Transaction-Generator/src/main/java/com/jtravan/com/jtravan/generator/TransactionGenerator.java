package com.jtravan.com.jtravan.generator;

import com.jtravan.model.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by johnravan on 3/31/16.
 */
public class TransactionGenerator {

    private static TransactionGenerator theInstance;

    private TransactionGenerator() {

    }

    public static final TransactionGenerator getInstance() {
        if(theInstance == null) {
            theInstance = new TransactionGenerator();
        }
        return theInstance;
    }

    public List<Transaction> generateRandomTransactions(int numOfOperations, int numOfTransactions) {

        if(numOfOperations <= 0 || numOfTransactions <= 0) {
            return Collections.emptyList();
        }

        List<Transaction> transactions = new LinkedList<Transaction>();

        for(int i = 0; i < numOfTransactions; i++) {

            Transaction transaction = new Transaction();

            for(int j = 0; j < numOfOperations; j++) {

                double random = Math.random();
                int operation = (int)random % 2;
                int resource = (int)random % 26;

                ResourceOperation resourceOperation = new ResourceOperation();
                resourceOperation.setExecutionTime((int)Math.random());
                resourceOperation.setOperation(Operation.getOperationByOperationNum(operation));
                resourceOperation.setResource(Resource.getResourceByResourceNum(resource));

                transaction.addResourceOperation(resourceOperation);

            }

            double random = Math.random();
            int category = (int)random % 4;
            transaction.setCategory(Category.getCategoryByCategoryNum(category));

            transactions.add(transaction);
        }

        return transactions;
    }
}
