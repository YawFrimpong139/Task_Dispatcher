//package org.example.model;

import org.example.model.Task;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void testTaskCreation() {
        Task task = new Task("TestTask", 5, "payload");
        assertNotNull(task.getId());
        assertEquals("TestTask", task.getName());
        assertEquals(5, task.getPriority());
        assertNotNull(task.getCreatedTimestamp());
        assertEquals("payload", task.getPayload());
        assertEquals(0, task.getRetryCount());
    }

    @Test
    void testInvalidPriority() {
        assertThrows(IllegalArgumentException.class, () -> new Task("Test", -1, "data"));
        assertThrows(IllegalArgumentException.class, () -> new Task("Test", 10, "data"));
    }

    @Test
    void testCompareTo() {
        Task highPriority = new Task("High", 9, "data");
        Task mediumPriority = new Task("Medium", 5, "data");
        Task lowPriority = new Task("Low", 1, "data");

        assertTrue(highPriority.compareTo(mediumPriority) < 0);
        assertTrue(mediumPriority.compareTo(lowPriority) < 0);
        assertEquals(0, mediumPriority.compareTo(new Task("Same", 5, "data")));
    }

    @Test
    void testSimulateProcessing() {
        Task task = new Task("Test", 5, "12345"); // 5 chars * 10 = 50ms base
        long time = task.simulateProcessing();
        assertTrue(time >= 10); // 50ms / priority 5 = 10ms minimum
    }

    @Test
    void testIncrementRetryCount() {
        Task task = new Task("Test", 3, "data");
        assertEquals(0, task.getRetryCount());
        task.incrementRetryCount();
        assertEquals(1, task.getRetryCount());
    }
}
