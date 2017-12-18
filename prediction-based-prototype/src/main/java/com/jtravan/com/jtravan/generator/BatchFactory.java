package com.jtravan.com.jtravan.generator;

import com.jtravan.model.ResourceOperation;
import com.jtravan.model.Transaction;

import java.util.Random;

public class BatchFactory {

    public static Transaction setAbortPercentage(Transaction transaction, int percentage) {

        Random random = new Random();
        float chance = random.nextInt(100);

        if (chance <= percentage) {
            int randomOperation = random.nextInt(transaction.getResourceOperationList().size());

            ResourceOperation resourceOperation = transaction.getResourceOperationList().get(randomOperation);
            resourceOperation.setAbortOperation(true);
        }

        return transaction;

    }


    public static Transaction setAbortPercentageBasedOnCategory(Transaction transaction) {

        if (transaction == null) {
            return transaction;
        }

        switch (transaction.getCategory()) {
            case HCHE:
                break;
            case HCLE:
                break;
            case LCHE:
                break;
            case LCLE:
                break;
            default:
                break;
        }

        return transaction;

    }

}
