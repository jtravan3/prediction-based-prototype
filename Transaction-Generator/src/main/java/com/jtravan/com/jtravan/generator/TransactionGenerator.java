package com.jtravan.com.jtravan.generator;

import com.jtravan.model.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by johnravan on 3/31/16.
 */
public class TransactionGenerator {

    private static TransactionGenerator theInstance;

    private TransactionGenerator() {}

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

                Random random = new Random();
                int randomInt = random.nextInt(200);
                int operation = randomInt % 2;
                int resource = randomInt % 26;

                ResourceOperation resourceOperation = new ResourceOperation();
                resourceOperation.setExecutionTime(random.nextInt(500));
                resourceOperation.setIsCommitOperation(false);
                resourceOperation.setOperation(Operation.getOperationByOperationNum(operation));
                resourceOperation.setResource(Resource.getResourceByResourceNum(resource));
                resourceOperation.setAssociatedTransaction(transaction);

                transaction.addResourceOperation(resourceOperation);

            }

            Random random = new Random();
            int randomInt2 = random.nextInt(500);
            int category = randomInt2 % 4;
            transaction.setCategory(Category.getCategoryByCategoryNum(category));
//            transaction.setCategory(Category.HCHE);

//            ResourceOperation commitOp = new ResourceOperation();
//            commitOp.setIsCommitOperation(true);
//            transaction.addResourceOperation(commitOp);

            transactions.add(transaction);
        }

        return transactions;
    }
}
