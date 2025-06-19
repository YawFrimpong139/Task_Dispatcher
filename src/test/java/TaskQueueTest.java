
import org.example.model.Task;
import org.example.queue.TaskQueue;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskQueueTest {
    @Test
    void testAddAndGetTask() {
        TaskQueue queue = new TaskQueue();
        Task task = new Task("Test", 5, "data");

        queue.addTask(task);
        assertEquals(1, queue.getQueueSize());

        Task retrieved = queue.getnextTask();
        assertEquals(task.getId(), retrieved.getId());
        assertEquals(0, queue.getQueueSize());
    }

    @Test
    void testPriorityOrder() {
        TaskQueue queue = new TaskQueue();
        Task high = new Task("High", 9, "data");
        Task low = new Task("Low", 1, "data");

        queue.addTask(low);
        queue.addTask(high);

        assertEquals(high, queue.getnextTask());
        assertEquals(low, queue.getnextTask());
    }

    @Test
    void testClear() {
        TaskQueue queue = new TaskQueue();
        queue.addTask(new Task("Test", 5, "data"));
        queue.clear();
        assertEquals(0, queue.getQueueSize());
    }
}