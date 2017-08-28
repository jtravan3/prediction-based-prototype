package com.jtravan.com.jtravan.generator;

import com.jtravan.model.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by johnravan on 4/10/16.
 */
public class ScheduleGenerator {

    private static ScheduleGenerator theInstance;
    private Map<Resource, Boolean> conflictMatrix;

    private ScheduleGenerator() {

        conflictMatrix = new HashMap<Resource, Boolean>();
        resetConflictMatrix();

    }

    public static final ScheduleGenerator getInstance() {

        if(theInstance == null) {
            theInstance = new ScheduleGenerator();
        }
        return theInstance;

    }

    public Schedule create1of2ExampleSchedule_NonConflicting(Category category) {

        Transaction transaction = new Transaction();
        transaction.setCategory(category);

        ResourceOperation ro1 = new ResourceOperation();
        ro1.setAssociatedTransaction(transaction);
        ro1.setExecutionTime(263);
        ro1.setIsCommitOperation(false);
        ro1.setOperation(Operation.WRITE);
        ro1.setResource(Resource.A);
        transaction.addResourceOperation(ro1);

        ResourceOperation ro2 = new ResourceOperation();
        ro2.setAssociatedTransaction(transaction);
        ro2.setExecutionTime(409);
        ro2.setIsCommitOperation(false);
        ro2.setOperation(Operation.WRITE);
        ro2.setResource(Resource.B);
        transaction.addResourceOperation(ro2);

        ResourceOperation ro3 = new ResourceOperation();
        ro3.setAssociatedTransaction(transaction);
        ro3.setExecutionTime(263);
        ro3.setIsCommitOperation(false);
        ro3.setOperation(Operation.WRITE);
        ro3.setResource(Resource.C);
        transaction.addResourceOperation(ro3);

        ResourceOperation ro4 = new ResourceOperation();
        ro4.setAssociatedTransaction(transaction);
        ro4.setExecutionTime(409);
        ro4.setIsCommitOperation(false);
        ro4.setOperation(Operation.WRITE);
        ro4.setResource(Resource.D);
        transaction.addResourceOperation(ro4);

        ResourceOperation ro5 = new ResourceOperation();
        ro5.setAssociatedTransaction(transaction);
        ro5.setExecutionTime(263);
        ro5.setIsCommitOperation(false);
        ro5.setOperation(Operation.WRITE);
        ro5.setResource(Resource.E);

//        if(category == Category.LCLE) {
//            ro5.setAbortOperation(true);
//        }

        transaction.addResourceOperation(ro5);

        ResourceOperation ro6 = new ResourceOperation();
        ro6.setAssociatedTransaction(transaction);
        ro6.setExecutionTime(409);
        ro6.setIsCommitOperation(false);
        ro6.setOperation(Operation.WRITE);
        ro6.setResource(Resource.F);
        transaction.addResourceOperation(ro6);

        ResourceOperation ro7 = new ResourceOperation();
        ro7.setAssociatedTransaction(transaction);
        ro7.setExecutionTime(348);
        ro7.setIsCommitOperation(false);
        ro7.setOperation(Operation.WRITE);
        ro7.setResource(Resource.G);
        transaction.addResourceOperation(ro7);

        ResourceOperation ro8 = new ResourceOperation();
        ro8.setAssociatedTransaction(transaction);
        ro8.setExecutionTime(632);
        ro8.setIsCommitOperation(false);
        ro8.setOperation(Operation.WRITE);
        ro8.setResource(Resource.H);
        transaction.addResourceOperation(ro8);

        ResourceOperation ro9 = new ResourceOperation();
        ro9.setAssociatedTransaction(transaction);
        ro9.setExecutionTime(121);
        ro9.setIsCommitOperation(false);
        ro9.setOperation(Operation.WRITE);
        ro9.setResource(Resource.I);
        transaction.addResourceOperation(ro9);

        List<Transaction> transactionList = new LinkedList<Transaction>();
        transactionList.add(transaction);

        return createSchedule(transactionList);
    }

    public Schedule create2of2ExampleSchedule_NonConflicting(Category category) {

        Transaction transaction = new Transaction();
        transaction.setCategory(category);

        ResourceOperation ro1 = new ResourceOperation();
        ro1.setAssociatedTransaction(transaction);
        ro1.setExecutionTime(263);
        ro1.setIsCommitOperation(false);
        ro1.setOperation(Operation.WRITE);
        ro1.setResource(Resource.J);
        transaction.addResourceOperation(ro1);

        ResourceOperation ro2 = new ResourceOperation();
        ro2.setAssociatedTransaction(transaction);
        ro2.setExecutionTime(409);
        ro2.setIsCommitOperation(false);
        ro2.setOperation(Operation.READ);
        ro2.setResource(Resource.K);
        transaction.addResourceOperation(ro2);

        ResourceOperation ro3 = new ResourceOperation();
        ro3.setAssociatedTransaction(transaction);
        ro3.setExecutionTime(263);
        ro3.setIsCommitOperation(false);
        ro3.setOperation(Operation.WRITE);
        ro3.setResource(Resource.L);
        transaction.addResourceOperation(ro3);

        ResourceOperation ro4 = new ResourceOperation();
        ro4.setAssociatedTransaction(transaction);
        ro4.setExecutionTime(409);
        ro4.setIsCommitOperation(false);
        ro4.setOperation(Operation.READ);
        ro4.setResource(Resource.M);
        transaction.addResourceOperation(ro4);

        ResourceOperation ro4_1 = new ResourceOperation();
        ro4_1.setAssociatedTransaction(transaction);
        ro4_1.setExecutionTime(409);
        ro4_1.setIsCommitOperation(false);
        ro4_1.setOperation(Operation.READ);
        ro4_1.setResource(Resource.M);
        transaction.addResourceOperation(ro4_1);

        ResourceOperation ro5 = new ResourceOperation();
        ro5.setAssociatedTransaction(transaction);
        ro5.setExecutionTime(263);
        ro5.setIsCommitOperation(false);
        ro5.setOperation(Operation.WRITE);
        ro5.setResource(Resource.N);
        transaction.addResourceOperation(ro5);

        ResourceOperation ro6 = new ResourceOperation();
        ro6.setAssociatedTransaction(transaction);
        ro6.setExecutionTime(409);
        ro6.setIsCommitOperation(false);
        ro6.setOperation(Operation.WRITE);
        ro6.setResource(Resource.O);
        transaction.addResourceOperation(ro6);

        ResourceOperation ro7 = new ResourceOperation();
        ro7.setAssociatedTransaction(transaction);
        ro7.setExecutionTime(333);
        ro7.setIsCommitOperation(false);
        ro7.setOperation(Operation.WRITE);
        ro7.setResource(Resource.P);
        transaction.addResourceOperation(ro7);

        ResourceOperation ro8 = new ResourceOperation();
        ro8.setAssociatedTransaction(transaction);
        ro8.setExecutionTime(204);
        ro8.setIsCommitOperation(false);
        ro8.setOperation(Operation.READ);
        ro8.setResource(Resource.Q);
        transaction.addResourceOperation(ro8);

        ResourceOperation ro9 = new ResourceOperation();
        ro9.setAssociatedTransaction(transaction);
        ro9.setExecutionTime(290);
        ro9.setIsCommitOperation(false);
        ro9.setOperation(Operation.WRITE);
        ro9.setResource(Resource.R);
        transaction.addResourceOperation(ro9);

        List<Transaction> transactionList = new LinkedList<Transaction>();
        transactionList.add(transaction);

        return createSchedule(transactionList);
    }

    public Schedule create1of2ExampleSchedule_Conflicting(Category category) {

        Transaction transaction = new Transaction();
        transaction.setCategory(category);

        ResourceOperation ro1 = new ResourceOperation();
        ro1.setAssociatedTransaction(transaction);
        ro1.setExecutionTime(263);
        ro1.setIsCommitOperation(false);
        ro1.setOperation(Operation.WRITE);
        ro1.setResource(Resource.A);
        transaction.addResourceOperation(ro1);

        ResourceOperation ro2 = new ResourceOperation();
        ro2.setAssociatedTransaction(transaction);
        ro2.setExecutionTime(409);
        ro2.setIsCommitOperation(false);
        ro2.setOperation(Operation.WRITE);
        ro2.setResource(Resource.C);
        transaction.addResourceOperation(ro2);

        ResourceOperation ro3 = new ResourceOperation();
        ro3.setAssociatedTransaction(transaction);
        ro3.setExecutionTime(263);
        ro3.setIsCommitOperation(false);
        ro3.setOperation(Operation.WRITE);
        ro3.setResource(Resource.B);
        transaction.addResourceOperation(ro3);

        ResourceOperation ro4 = new ResourceOperation();
        ro4.setAssociatedTransaction(transaction);
        ro4.setExecutionTime(409);
        ro4.setIsCommitOperation(false);
        ro4.setOperation(Operation.WRITE);
        ro4.setResource(Resource.D);
        transaction.addResourceOperation(ro4);

        ResourceOperation ro5 = new ResourceOperation();
        ro5.setAssociatedTransaction(transaction);
        ro5.setExecutionTime(263);
        ro5.setIsCommitOperation(false);
        ro5.setOperation(Operation.WRITE);
        ro5.setResource(Resource.E);

//        if(category == Category.LCLE) {
//            ro5.setAbortOperation(true);
//        }

        transaction.addResourceOperation(ro5);

        ResourceOperation ro6 = new ResourceOperation();
        ro6.setAssociatedTransaction(transaction);
        ro6.setExecutionTime(409);
        ro6.setIsCommitOperation(false);
        ro6.setOperation(Operation.WRITE);
        ro6.setResource(Resource.F);
        transaction.addResourceOperation(ro6);

        List<Transaction> transactionList = new LinkedList<Transaction>();
        transactionList.add(transaction);

        return createSchedule(transactionList);
    }

    public Schedule create2of2ExampleSchedule_Conflicting(Category category) {

        Transaction transaction = new Transaction();
        transaction.setCategory(category);

        ResourceOperation ro1 = new ResourceOperation();
        ro1.setAssociatedTransaction(transaction);
        ro1.setExecutionTime(263);
        ro1.setIsCommitOperation(false);
        ro1.setOperation(Operation.WRITE);
        ro1.setResource(Resource.B);
        transaction.addResourceOperation(ro1);

        ResourceOperation ro2 = new ResourceOperation();
        ro2.setAssociatedTransaction(transaction);
        ro2.setExecutionTime(409);
        ro2.setIsCommitOperation(false);
        ro2.setOperation(Operation.WRITE);
        ro2.setResource(Resource.D);
        transaction.addResourceOperation(ro2);

        ResourceOperation ro3 = new ResourceOperation();
        ro3.setAssociatedTransaction(transaction);
        ro3.setExecutionTime(263);
        ro3.setIsCommitOperation(false);
        ro3.setOperation(Operation.WRITE);
        ro3.setResource(Resource.A);
        transaction.addResourceOperation(ro3);

        ResourceOperation ro4 = new ResourceOperation();
        ro4.setAssociatedTransaction(transaction);
        ro4.setExecutionTime(409);
        ro4.setIsCommitOperation(false);
        ro4.setOperation(Operation.WRITE);
        ro4.setResource(Resource.C);
        transaction.addResourceOperation(ro4);

        ResourceOperation ro5 = new ResourceOperation();
        ro5.setAssociatedTransaction(transaction);
        ro5.setExecutionTime(263);
        ro5.setIsCommitOperation(false);
        ro5.setOperation(Operation.WRITE);
        ro5.setResource(Resource.F);
        transaction.addResourceOperation(ro5);

        ResourceOperation ro6 = new ResourceOperation();
        ro6.setAssociatedTransaction(transaction);
        ro6.setExecutionTime(409);
        ro6.setIsCommitOperation(false);
        ro6.setOperation(Operation.WRITE);
        ro6.setResource(Resource.E);
        transaction.addResourceOperation(ro6);

        List<Transaction> transactionList = new LinkedList<Transaction>();
        transactionList.add(transaction);

        return createSchedule(transactionList);
    }

    public Schedule createSchedule(List<Transaction> transactions) {

        if(transactions == null || transactions.isEmpty()) {
            return null;
        }

        if(transactions.size() == 1) {
            Schedule rtnSchedule = new Schedule();
            rtnSchedule.setCategory(transactions.get(0).getCategory());
            rtnSchedule.setResourceOperationList(transactions.get(0).getResourceOperationList());
            transactions.get(0).setScheduleTransactionIsApartOf(rtnSchedule);
            return rtnSchedule;
        }

        Transaction t1 = transactions.remove(0);
        Transaction t2 = transactions.remove(0);

        setConflictsForTransactions(t1, t2);

        Transaction miniSchedule = createTransactionFromConflicts(t1,t2);
        resetConflictMatrix();

        transactions.add(miniSchedule);
        return createSchedule(transactions);

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

                // add all conflicting operations for transaction 1
                for(ResourceOperation resourceOperation : transaction1.getAndRemoveOperationsByResource(entry.getKey())) {
                    rtnTransaction.addResourceOperation(resourceOperation);
                }

                // add all conflicting operations for transaction 2
                for(ResourceOperation resourceOperation : transaction2.getAndRemoveOperationsByResource(entry.getKey())) {
                    rtnTransaction.addResourceOperation(resourceOperation);
                }

            }
        }

        // add all conflicting operations for transaction 1
        for(ResourceOperation resourceOperation : transaction1.getResourceOperationList()) {
            rtnTransaction.addResourceOperation(resourceOperation);
        }

        // add all conflicting operations for transaction 2
        for(ResourceOperation resourceOperation : transaction2.getResourceOperationList()) {
            rtnTransaction.addResourceOperation(resourceOperation);
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