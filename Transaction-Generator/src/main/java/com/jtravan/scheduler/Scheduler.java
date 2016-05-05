package com.jtravan.scheduler;

import com.jtravan.model.*;

import java.util.*;

/**
 * Created by johnravan on 4/10/16.
 */
public class Scheduler {

    private static Scheduler theInstance;
    private Map<Resource, Boolean> conflictMatrix;

    private Scheduler() {
        conflictMatrix = new HashMap<Resource, Boolean>();
        resetConflictMatrix();
    }

    public static final Scheduler getInstance() {
        if(theInstance == null) {
            theInstance = new Scheduler();
        }
        return theInstance;
    }


    public Schedule createSchedule(List<Transaction> transactions) {

        if(transactions == null || transactions.isEmpty()) {
            return null;
        }

        if(transactions.size() == 1) {
            Schedule rtnSchedule = new Schedule();
            rtnSchedule.setResourceOperationList(transactions.get(0).getResourceOperationList());
            return rtnSchedule;
        }

        Transaction t1 = transactions.remove(0);
        Transaction t2 = transactions.remove(0);

        setConflictsForTransactions(t1, t2);

        Transaction miniSchedule = createTransactionFromConflicts(t1,t2);
        resetConflictMatrix();

        transactions.add(miniSchedule);
        createSchedule(transactions);

        return null;

    }

    private void setConflictsForTransactions(Transaction transaction1, Transaction transaction2) {

        if(transaction1 == null || transaction2 == null) {
            return;
        }

        for(ResourceOperation operation1 : transaction1.getResourceOperationList()) {
            for (ResourceOperation operation2 : transaction2.getResourceOperationList()) {
                if(operation1.getResource() == operation2.getResource() &&
                        operation1.getOperation() == Operation.WRITE ||
                        operation2.getOperation() == Operation.WRITE) {
                    conflictMatrix.remove(operation1.getResource());
                    conflictMatrix.put(operation1.getResource(), true);
                }
            }
        }

    }

    private Transaction createTransactionFromConflicts(Transaction transaction1, Transaction transaction2) {

        if(transaction1 == null || transaction2 == null) {
            return null;
        }

        Transaction rtnTransaction = new Transaction();

        for(Map.Entry<Resource, Boolean> entry : conflictMatrix.entrySet()) {
            if(entry.getValue()) {

                for(ResourceOperation operation : transaction1.getAndRemoveOperationsByResource(entry.getKey())) {
                    rtnTransaction.addResourceOperation(operation);
                }

                for(ResourceOperation operation : transaction2.getAndRemoveOperationsByResource(entry.getKey())) {
                    rtnTransaction.addResourceOperation(operation);
                }

            }
        }


        return rtnTransaction;

    }

    private void resetConflictMatrix() {

        conflictMatrix.clear();

        for(Resource r : Resource.values()) {
            conflictMatrix.put(r, false);
        }

    }

}
