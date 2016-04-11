package com.jtravan.scheduler;

import com.jtravan.model.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

        Schedule schedule = new Schedule();

        while(!transactions.isEmpty()) {
            Iterator<Transaction> i = transactions.iterator();
            while(i.hasNext()) {

                Transaction t = i.next();
                if(!t.hasMoreResourceOperations()) {
                    i.remove();
                    continue;
                }

                ResourceOperation op = t.getNextResourceOperation();

                // if there is no conflict
                if(op.getResource() == null) {
                    continue;
                }

                if(!conflictMatrix.get(op.getResource())) {

                    if(op.getOperation() == Operation.WRITE) {
                        conflictMatrix.put(op.getResource(), true);
                    }

                    schedule.addResourceOperation(op);

                } else {

                    // add the rest of the transaction to the schedule
                    schedule.addResourceOperation(op);
                    while(t.hasMoreResourceOperations()) {
                        schedule.addResourceOperation(t.getNextResourceOperation());
                    }
                }
            }
        }

        return schedule;
    }

    private void resetConflictMatrix() {

        conflictMatrix.clear();

        for(Resource r : Resource.values()) {
            conflictMatrix.put(r, false);
        }

    }

}
