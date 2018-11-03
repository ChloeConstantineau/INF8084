package ca.polymtl.inf8480.tp2.shared;

import java.util.ArrayList;
import java.util.List;

public class Task{

    public List<Operation> operations;

    public Task(ArrayList<Operation> operations){
        this.operations = operations;
    }
}