package com.jtravan.tester;

import com.jtravan.com.jtravan.generator.ScheduleGenerator;
import com.jtravan.com.jtravan.generator.TransactionGenerator;
import com.jtravan.model.Schedule;
import com.jtravan.model.Transaction;
import com.jtravan.scheduler.PredictionBasedScheduler;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by johnravan on 1/26/17.
 */
public class MultiplePredictionBasedSchedulerTester {

    private static final int NUM_OF_SCHEDULERS_EXECUTING = 2;
    private static final int NUM_OF_OPERATIONS_PER_TRANSACTION = 20;
    private static final int NUM_OF_TRANSACTIONS = 1;

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {

        List<PredictionBasedScheduler> predictionBasedSchedulerList = new LinkedList<PredictionBasedScheduler>();

        for(int i = 0; i < NUM_OF_SCHEDULERS_EXECUTING; i++) {
            TransactionGenerator transactionGenerator = TransactionGenerator.getInstance();
            List<Transaction> transactionList = transactionGenerator.generateRandomTransactions(NUM_OF_OPERATIONS_PER_TRANSACTION, NUM_OF_TRANSACTIONS);

            ScheduleGenerator scheduleGenerator = ScheduleGenerator.getInstance();
//            Schedule schedule;
//            if (i == 0) {
//                schedule = scheduleGenerator.create1of2ElevateSchedule();
//            } else {
//                schedule = scheduleGenerator.create2of2ElevateSchedule();
//            }

            Schedule schedule = scheduleGenerator.createSchedule(transactionList);
            System.out.println("Schedule to be executed: " + schedule);

            predictionBasedSchedulerList.add(new PredictionBasedScheduler(schedule, "Scheduler " + i));
        }

        ExecutorService executorService = Executors.newCachedThreadPool();
        CyclicBarrier gate = new CyclicBarrier(NUM_OF_SCHEDULERS_EXECUTING + 1);

        final long startTime = System.currentTimeMillis();

        for (PredictionBasedScheduler predictionBasedScheduler: predictionBasedSchedulerList) {
            executorService.execute(new PredictionBasedSchedulerTask(predictionBasedScheduler, gate));
        }

        System.out.println("Starting now...");
        Thread.sleep(1000);

        gate.await();

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);

        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime));
    }

    private static class PredictionBasedSchedulerTask implements Runnable {

        private PredictionBasedScheduler predictionBasedScheduler;
        private CyclicBarrier gate;

        public PredictionBasedSchedulerTask(PredictionBasedScheduler predictionBasedScheduler, CyclicBarrier gate) {
            this.predictionBasedScheduler = predictionBasedScheduler;
            this.gate = gate;
        }

        public void run() {
            try {
                gate.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

            boolean isFinished = predictionBasedScheduler.executeSchedule();

            if (!isFinished) {
                System.out.println(predictionBasedScheduler.getSchedulerName() + ": Aborted. Waiting for other schedule to finish before retrying execution");
                try {
                    synchronized (this) {
                        wait(2000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(predictionBasedScheduler.getSchedulerName() + ": Other schedule finished. Now retrying...");
                PredictionBasedScheduler predictionBasedScheduler_ForAbort = new PredictionBasedScheduler(predictionBasedScheduler.getSchedule(), predictionBasedScheduler.getSchedulerName());
                predictionBasedScheduler_ForAbort.executeSchedule();
            }
        }
    }
}
