package org.example.model;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@ToString
public class Task implements Comparable<Task>{
    private final UUID id;
    private final String name;
    private final int priority;
    private final Instant createdTimestamp;
    private final String payload;
    private int retryCount;

    public Task(String name, int priority, String payload) {
        if (priority < 0 || priority > 9) {
            throw new IllegalArgumentException("Priority must be between 0-9");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.priority = priority;
        this.createdTimestamp = Instant.now();
        this.payload = payload != null ? payload : "";
        this.retryCount = 0;
    }

    public void incrementRetryCount(){
        this.retryCount++;
    }


    @Override
    public int compareTo(Task other) {
        return Integer.compare(other.priority, this.priority);
    }

    public long simulateProcessing(){
        long processingTime = Math.max(100, payload.length() * 10L);
        processingTime = processingTime / (priority > 0 ? priority : 1);

        try {
            Thread.sleep(processingTime);
        }catch(Exception e){
            Thread.currentThread().interrupt();
        }

        if(ThreadLocalRandom.current().nextDouble() <  0.1){
            throw new RuntimeException("Simulated processing error");
        }

        return processingTime;
    }
}

