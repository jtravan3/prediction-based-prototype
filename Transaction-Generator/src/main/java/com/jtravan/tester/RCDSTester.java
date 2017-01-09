package com.jtravan.tester;

import com.jtravan.com.jtravan.generator.TransactionGenerator;
import com.jtravan.model.*;

import java.util.List;

/**
 * Created by johnravan on 6/22/16.
 */
public class RCDSTester {

    public static void main(String[] args) {

        TransactionGenerator generator = TransactionGenerator.getInstance();
        List<Transaction> transactions = generator.generateRandomTransactions(20, 20);

        ResourceCategoryDataStructure rcds = ResourceCategoryDataStructure.getReadInstance();

        for(Transaction transaction : transactions) {
            for(ResourceOperation resourceOperation: transaction.getResourceOperationList()) {
                rcds.insertResourceOperationForResource(resourceOperation.getResource(), resourceOperation);
            }
        }

        for(Resource resource : rcds.getResourceSet()) {
            Heap resourceHeap = rcds.getHeapForResource(resource);
            System.out.println("Highest category for resource: \"" +
                    rcds.getHighestPriorityForResource(resource).getAssociatedTransaction().getCategory().name() + "\"");
            System.out.println("Heap for Resource \"" + resource + "\": ");
            rcds.printHeap(resourceHeap);
        }
    }
}
