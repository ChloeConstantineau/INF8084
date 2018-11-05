package ca.polymtl.inf8480.tp2.shared;

import java.io.Serializable;
import java.util.List;

public class Task implements Serializable {

    public List<Operation> operations;
    public int id;

    public Task(List<Operation> operations) {
        this.operations = operations;
    }

    public Task(int id, List<Operation> operations) {
        this.id = id;
        this.operations = operations;
    }
}