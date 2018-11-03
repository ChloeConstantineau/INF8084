package ca.polymtl.inf8480.tp1.shared;

import java.io.Serializable;

public class Lock implements Serializable{
    public boolean isLocked;
    public String message;

    public Lock(boolean lock, String output){
        isLocked = lock;
        message = output;
    }

    @Override
    public String toString() {
        return "Lock [isLocked=" + isLocked+ ", message=" + message
                + "]";
    }

}
