

import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.queue.TaskQueueManager;
import org.example.worker.TaskConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class TaskConsumerTest {
    private TaskQueueManager manager;
    private AtomicInteger processedCount;
    private AtomicInteger failedCount;
    private TaskConsumer consumer;

    @BeforeEach
    void setUp() {
        manager = new TaskQueueManager();
        processedCount = new AtomicInteger(0);
        failedCount = new AtomicInteger(0);
        consumer = new TaskConsumer(manager, processedCount, failedCount);
    }

    @Test
    void testSuccessfulProcessing() throws InterruptedException {
        Task task = new Task("Test", 5, "data");
        manager.submitTask(task);

        // Run consumer in current thread
        consumer.run();

        assertEquals(1, processedCount.get());
        assertEquals(0, failedCount.get());
        assertEquals(TaskStatus.COMPLETED, manager.getTaskStatus(task.getId()));
    }

    @Test
    void testTaskRetry() {
        Task failingTask = new Task("Fail", 1, "fail") {
            @Override
            public long simulateProcessing() {
                throw new RuntimeException("Simulated failure");
            }
        };

        manager.submitTask(failingTask);
        consumer.run();

        assertEquals(1, failedCount.get());
        assertEquals(1, failingTask.getRetryCount());
        assertEquals(TaskStatus.SUBMITTED, manager.getTaskStatus(failingTask.getId()));
    }
}

