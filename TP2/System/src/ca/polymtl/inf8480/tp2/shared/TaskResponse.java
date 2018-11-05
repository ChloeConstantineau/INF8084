package ca.polymtl.inf8480.tp2.shared;

import java.io.Serializable;

public class TaskResponse implements Serializable {
    public int result;
    public String details;

    public static TaskResponse of(int result, String details) {
        return new TaskResponse(result, details);
    }

    private TaskResponse(int result, String details) {
        this.result = result;
        this.details = details;
    }
}