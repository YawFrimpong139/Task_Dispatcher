

import org.example.monitor.QueueMonitor;
import org.example.queue.TaskQueueManager;
import org.example.worker.WorkerPool;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class QueueMonitorTest {
    @Test
    void testStatusExport() throws Exception {
        TaskQueueManager manager = new TaskQueueManager();
        WorkerPool pool = new WorkerPool(manager, 2);

        // Create test directory
        File testDir = new File("test_exports");
        testDir.mkdir();

        QueueMonitor monitor = new QueueMonitor(manager, pool, 1, testDir.getAbsolutePath());
        monitor.exportStatusToJson();

        // Verify file was created
        File[] files = testDir.listFiles((dir, name) -> name.endsWith(".json"));
        assertTrue(files != null && files.length > 0);

        // Cleanup
        for (File f : files) f.delete();
        testDir.delete();
        pool.shutDown();
    }
}

