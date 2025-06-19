

import org.example.model.Task;
import org.example.model.TaskStatus;
import org.example.queue.TaskQueueManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class TaskQueueManagerTest {
    private TaskQueueManager manager;
    private Task testTask;

    @BeforeEach
    void setUp() {
        manager = new TaskQueueManager();
        testTask = new Task("Test", 5, "data");
    }

    @Test
    void testSubmitTask() {
        manager.submitTask(testTask);
        assertEquals(1, manager.getQueueSize());
        assertEquals(TaskStatus.SUBMITTED, manager.getTaskStatus(testTask.getId()));
        assertEquals(testTask, manager.getTask(testTask.getId()));
    }

    @Test
    void testTaskLifecycle() throws InterruptedException {
        manager.submitTask(testTask);

        Task retrieved = manager.getNextTask();
        assertEquals(testTask.getId(), retrieved.getId());
        assertEquals(TaskStatus.PROCESSING, manager.getTaskStatus(testTask.getId()));

        manager.markTaskCompleted(testTask.getId());
        assertEquals(TaskStatus.COMPLETED, manager.getTaskStatus(testTask.getId()));

        manager.markTaskFailed(testTask.getId());
        assertEquals(TaskStatus.FAILED, manager.getTaskStatus(testTask.getId()));
    }

    @Test
    void testClear() {
        manager.submitTask(testTask);
        manager.clear();
        assertEquals(0, manager.getQueueSize());
        assertNull(manager.getTask(testTask.getId()));
    }
}