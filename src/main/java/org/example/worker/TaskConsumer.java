package org.example.worker;


import org.example.model.Task;
import org.example.queue.TaskQueueManager;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicInteger;

public class TaskConsumer implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(TaskConsumer.class);
    private static final int MAX_RETRIES = 3;

    private final TaskQueueManager taskQueueManager;
    private final AtomicInteger tasksProcessedCount;
    private final AtomicInteger tasksFailedCount;

    public TaskConsumer(TaskQueueManager taskQueueManager, AtomicInteger tasksProcessedCount, AtomicInteger tasksFailedCount) {
        this.taskQueueManager = taskQueueManager;
        this.tasksProcessedCount = tasksProcessedCount;
        this.tasksFailedCount = tasksFailedCount;
    }


    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Task task = taskQueueManager.getNextTask();
                logger.info("Processing task: {}", task);

                try {
                    long processingTime = task.simulateProcessing();
                    taskQueueManager.markTaskCompleted(task.getId());
                    tasksProcessedCount.incrementAndGet();
                    logger.info("Completed task {} in {} ms", task.getId(), processingTime);
                } catch (Exception e) {
                    handleTaskFailure(task, e);
                }
            } catch (InterruptedException e) {
                // Restore the interrupt status
                Thread.currentThread().interrupt();
                logger.info("Consumer thread interrupted");
                break;  // Exit the loop on interruption
            }
        }
        logger.info("Consumer thread exiting");
    }

    private void handleTaskFailure(Task task, Exception e) {
        task.incrementRetryCount();
        tasksFailedCount.incrementAndGet();
        logger.error("Failed to process task {} (attempt {}/{}): {}",
                task.getId(), task.getRetryCount(), MAX_RETRIES, e.getMessage());

        if (task.getRetryCount() < MAX_RETRIES) {
            logger.info("Re-queueing task {} for retry", task.getId());
            taskQueueManager.submitTask(task);
        } else {
            taskQueueManager.markTaskFailed(task.getId());
            logger.error("Task {} failed after {} retries", task.getId(), MAX_RETRIES);
        }
    }
}

