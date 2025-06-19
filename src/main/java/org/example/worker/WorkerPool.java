package org.example.worker;


import org.example.queue.TaskQueueManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;



public class WorkerPool {
    private final ExecutorService executorService;
    private final TaskQueueManager taskQueueManager;
    private final int poolSize;
    private final AtomicInteger tasksProcessedCount =  new AtomicInteger(0);
    private final AtomicInteger tasksFailedCount =  new AtomicInteger(0);

    public WorkerPool(TaskQueueManager taskQueueManager, int poolSize) {
        this.taskQueueManager = taskQueueManager;
        this.poolSize = poolSize;
        this.executorService = Executors.newFixedThreadPool(poolSize);
        initializeWorkers();
    }

    private void initializeWorkers() {
        for (int i = 0; i < poolSize; i++) {
            executorService.submit(new TaskConsumer(taskQueueManager, tasksProcessedCount, tasksFailedCount));
        }
    }

    public void shutDown(){
        executorService.shutdown();
        try{
            if(!executorService.awaitTermination(60, TimeUnit.SECONDS)){
                executorService.shutdownNow();
            }
        }catch(InterruptedException e){
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public int getTasksProcessedCount() {
        return tasksProcessedCount.get();
    }

    public int getTasksFailedCount() {
        return tasksFailedCount.get();
    }

    public int getActiveThreads(){
        return ((ExecutorService) executorService).isShutdown() ? 0 :
                poolSize - ((java.util.concurrent.ThreadPoolExecutor) executorService).getActiveCount();
    }
}
