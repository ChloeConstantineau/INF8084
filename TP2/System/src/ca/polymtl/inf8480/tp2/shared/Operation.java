package ca.polymtl.inf8480.tp2.shared;

public class Operation {
    public OperationType type;
    public int value;

    public Operation(OperationType type, int value) {
        this.type = type;
        this.value = value;
    }
}
