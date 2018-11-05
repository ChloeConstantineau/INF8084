package ca.polymtl.inf8480.tp2.shared;

import java.io.Serializable;

public class TaskResult implements Serializable {
    public int result;
    public Exception hadFailure;

    public static TaskResult of(int result, Exception e) {
        return new TaskResult(result, e);
    }

    private TaskResult(int result, Exception e) {
        this.result = result;
        this.hadFailure = e;
    }
}