package org.example.queue;


import org.example.model.Task;
import org.example.model.TaskStatus;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class TaskQueueManager {
    private final TaskQueue taskQueue;
    private final Map<UUID, TaskStatus > taskStatusMap;
    private final Map<UUID, Task> taskMap;


    public TaskQueueManager() {
        this.taskQueue = new TaskQueue();
        this.taskStatusMap = new ConcurrentHashMap<>();
        this.taskMap = new ConcurrentHashMap<>();
    }
    public void submitTask(Task task) {
        taskQueue.addTask(task);
        taskStatusMap.put(task.getId(), TaskStatus.SUBMITTED);
        taskMap.put(task.getId(), task);
    }

    public Task getNextTask() throws  InterruptedException {
        Task task = taskQueue.getnextTask();
        taskStatusMap.put(task.getId(), TaskStatus.PROCESSING);
        return task;
    }

    public void markTaskCompleted(UUID taskId) {
        taskStatusMap.put(taskId, TaskStatus.COMPLETED);
    }

    public void markTaskFailed(UUID taskId) {
        taskStatusMap.put(taskId, TaskStatus.FAILED);
    }

    public TaskStatus getTaskStatus(UUID taskId) {
        return taskStatusMap.getOrDefault(taskId, TaskStatus.SUBMITTED);
    }

    public Task getTask(UUID taskId) {
        return taskMap.get(taskId);
    }

    public int getQueueSize() {
        return taskQueue.getQueueSize();
    }

    public Map<UUID, TaskStatus> getTaskStatusMap() {
        return new ConcurrentHashMap<>(taskStatusMap);
    }

    public void clear(){
        taskQueue.clear();
        taskStatusMap.clear();
        taskMap.clear();
    }

}

