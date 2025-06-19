package org.example.worker;

import org.example.model.Task;
import org.example.queue.TaskQueueManager;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TaskProducer implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(TaskProducer.class);
    private static final String[] TASK_NAMES = {
            "DataProcessing", "ReportGeneration", "ImageResize",
            "VideoEncoding", "Backup", "Cleanup", "Notification"
    };

    private final TaskQueueManager taskQueueManager;
    private final String producerId;
    private final int maxTasksPerBatch;
    private final Random random = new Random();

    public TaskProducer(TaskQueueManager taskQueueManager, String producerId, int maxTasksPerBatch) {
        this.taskQueueManager = taskQueueManager;
        this.producerId = producerId;
        this.maxTasksPerBatch = maxTasksPerBatch;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                int tasksToGenerate = 1 + random.nextInt(maxTasksPerBatch);
                generateTasks(tasksToGenerate);

                // Sleep for random interval between 1-5 seconds
                TimeUnit.MILLISECONDS.sleep(1000 + random.nextInt(4000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("Producer {} interrupted", producerId);
        }
    }

    private void generateTasks(int count) {
        for (int i = 0; i < count; i++) {
            String taskName = TASK_NAMES[random.nextInt(TASK_NAMES.length)];
            int priority = random.nextInt(10); // 0-9, higher is more priority
            String payload = generateRandomPayload();

            Task task = new Task(producerId + "-" + taskName, priority, payload);
            taskQueueManager.submitTask(task);
            logger.debug("Submitted task: {}", task);
        }
        logger.info("Producer {} submitted {} tasks", producerId, count);
    }

    private String generateRandomPayload() {
        int size = 100 + random.nextInt(900); // 100-1000 bytes
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            sb.append((char) ('a' + random.nextInt(26)));
        }
        return sb.toString();
    }
}
