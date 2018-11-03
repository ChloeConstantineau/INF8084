package ca.polymtl.inf8480.tp2.shared;

import java.io.Serializable;


enum TaskStatus implements Serializable {
    Accepted,
    Rejected
}

public class TaskResponse implements Serializable {

    public TaskStatus Status;
    public int response;
    public String Details;
}