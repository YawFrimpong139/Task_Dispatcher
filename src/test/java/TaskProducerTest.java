

import org.example.queue.TaskQueueManager;
import org.example.worker.TaskProducer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskProducerTest {
    @Test
    void testTaskGeneration() {
        TaskQueueManager manager = new TaskQueueManager();
        TaskProducer producer = new TaskProducer(manager, "TestProducer", 5);

        producer.generateTasks(3);
        assertEquals(3, manager.getQueueSize());
    }

    @Test
    void testPayloadGeneration() {
        TaskProducer producer = new TaskProducer(null, "Test", 1);
        String payload = producer.generateRandomPayload();
        assertNotNull(payload);
        assertTrue(payload.length() >= 100 && payload.length() <= 1000);
    }
}