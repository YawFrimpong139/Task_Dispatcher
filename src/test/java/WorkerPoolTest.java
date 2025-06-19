

import org.example.queue.TaskQueueManager;
import org.example.worker.WorkerPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WorkerPoolTest {
    private TaskQueueManager manager;
    private WorkerPool pool;

    @BeforeEach
    void setUp() {
        manager = new TaskQueueManager();
        pool = new WorkerPool(manager, 3);
    }

    @AfterEach
    void tearDown() {
        pool.shutDown();
    }

    @Test
    void testInitialization() {
        assertEquals(0, pool.getTasksProcessedCount());
        assertEquals(0, pool.getTasksFailedCount());
        assertTrue(pool.getActiveThreads() >= 0);
    }

    @Test
    void testShutdown() throws InterruptedException {
        pool.shutDown();
        assertTrue(pool.getActiveThreads() == 0);
    }
}