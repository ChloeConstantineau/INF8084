package ca.polymtl.inf8480.tp2.shared;

import java.util.ArrayList;
import java.util.List;

public class Task{

    List<Operation> Operations;

    public Task(){
        Operations = new ArrayList<Operation>();
    }

    public Task(List<Operation> operations){
        Operations = operations;
    }
}