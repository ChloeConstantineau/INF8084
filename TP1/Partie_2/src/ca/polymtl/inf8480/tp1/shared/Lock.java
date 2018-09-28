package ca.polymtl.inf8480.tp1.shared;

public class Lock {
    boolean isLocked;
    String message;

    public Lock(boolean lock, String output){
        isLocked = lock;
        message = output;
    }
}
