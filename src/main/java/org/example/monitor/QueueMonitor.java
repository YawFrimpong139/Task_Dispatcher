package org.example.monitor;


import org.example.queue.TaskQueueManager;
import org.example.worker.WorkerPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QueueMonitor implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(QueueMonitor.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final TaskQueueManager taskQueueManager;
    private final WorkerPool workerPool;
    private final int monitorIntervalSec;
    private final String statusExportPath;

    public QueueMonitor(TaskQueueManager taskQueueManager, WorkerPool workerPool,
                        int monitorIntervalSec, String statusExportPath) {
        this.taskQueueManager = taskQueueManager;
        this.workerPool = workerPool;
        this.monitorIntervalSec = monitorIntervalSec;
        this.statusExportPath = statusExportPath;
    }

    @Override
    public void run() {
        try {
            long lastExportTime = 0;
            while (!Thread.currentThread().isInterrupted()) {
                // Log system status
                logSystemStatus();

                // Export status to JSON every minute if path is provided
                if (statusExportPath != null && System.currentTimeMillis() - lastExportTime > 60000) {
                    exportStatusToJson();
                    lastExportTime = System.currentTimeMillis();
                }

                TimeUnit.SECONDS.sleep(monitorIntervalSec);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.info("Monitor thread interrupted");
        }
    }

    private void logSystemStatus() {
        logger.info("System Status - Queue Size: {}, Active Workers: {}, Processed: {}, Failed: {}",
                taskQueueManager.getQueueSize(),
                workerPool.getActiveThreads(),
                workerPool.getTasksProcessedCount(),
                workerPool.getTasksFailedCount());
    }

    public void exportStatusToJson() {
        try {
            // Create directory if it doesn't exist
            File outputDir = new File(statusExportPath);
            if (!outputDir.exists()) {
                if (!outputDir.mkdirs()) {
                    logger.error("Failed to create directory: {}", statusExportPath);
                    return;
                }
            }

            Map<String, Object> status = Map.of(
                    "timestamp", Instant.now().toString(),
                    "queueSize", taskQueueManager.getQueueSize(),
                    "processedTasks", workerPool.getTasksProcessedCount(),
                    "failedTasks", workerPool.getTasksFailedCount(),
                    "taskStatuses", taskQueueManager.getTaskStatusMap()
            );

            File outputFile = new File(outputDir, "task_status_" + System.currentTimeMillis() + ".json");

            // Write with temporary file first, then rename (atomic operation)
            File tempFile = new File(outputDir, "task_status_" + System.currentTimeMillis() + ".tmp");
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(tempFile, status);

            // Rename temp file to final file
            if (!tempFile.renameTo(outputFile)) {
                logger.error("Failed to rename temp file to final output file");
            }

            logger.info("Exported system status to {}", outputFile.getAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to export system status to {}", statusExportPath, e);
        }
    }
}
