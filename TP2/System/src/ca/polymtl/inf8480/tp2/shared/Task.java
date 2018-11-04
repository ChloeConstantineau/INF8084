package ca.polymtl.inf8480.tp2.shared;

import java.io.Serializable;
import java.util.List;

public class Task implements Serializable {

    public List<Operation> operations;

    public Task(List<Operation> operations){
        this.operations = operations;
    }

    public void addOperation(Operation operation){
        operations.add(operation);
    }

    public void addOperation(OperationType type, int value){
        operations.add(Operation.of(type,value));
    }
}