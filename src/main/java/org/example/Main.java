package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import org.example.monitor.QueueMonitor;
import org.example.queue.TaskQueueManager;
import org.example.worker.TaskProducer;
import org.example.worker.WorkerPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final int PRODUCER_COUNT = 3;
    private static final int WORKER_POOL_SIZE = 5;
    private static final int MAX_TASKS_PER_BATCH = 5;
    private static final int MONITOR_INTERVAL_SEC = 5;

    public static void main(String[] args) {
        // Initialize components
        TaskQueueManager taskQueueManager = new TaskQueueManager();
        WorkerPool workerPool = new WorkerPool(taskQueueManager, WORKER_POOL_SIZE);

        // Use system property or environment variable for path
        String exportPath = System.getProperty("status.export.path",
                System.getenv().getOrDefault("STATUS_EXPORT_PATH", "status_exports"));
        //QueueMonitor monitor = new QueueMonitor(taskQueueManager, workerPool, MONITOR_INTERVAL_SEC, exportPath);

        // Start producers
        ExecutorService producerExecutor = Executors.newFixedThreadPool(PRODUCER_COUNT);
        for (int i = 0; i < PRODUCER_COUNT; i++) {
            producerExecutor.submit(new TaskProducer(taskQueueManager, "Producer-" + (i + 1), MAX_TASKS_PER_BATCH));
        }

        // Start monitor
        QueueMonitor monitor = new QueueMonitor(taskQueueManager, workerPool, MONITOR_INTERVAL_SEC, exportPath);
        Thread monitorThread = new Thread(monitor);
        monitorThread.setDaemon(true);
        monitorThread.start();

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down system...");

            // Shutdown producers first
            producerExecutor.shutdownNow();

            // Then shutdown workers
            workerPool.shutDown();

            try {
                if (!producerExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.err.println("Producers did not terminate gracefully");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println("System shutdown complete");
        }));

        // Keep main thread alive
        try {
            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}