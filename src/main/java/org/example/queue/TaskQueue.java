package org.example.queue;


import org.example.model.Task;

import java.util.concurrent.PriorityBlockingQueue;

public class TaskQueue {
    private final PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<>();

    public void addTask(Task task) {
        queue.add(task);
    }

    public Task getnextTask() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int getQueueSize() {
        return queue.size();
    }

    public void clear() {
        queue.clear();
    }


}
