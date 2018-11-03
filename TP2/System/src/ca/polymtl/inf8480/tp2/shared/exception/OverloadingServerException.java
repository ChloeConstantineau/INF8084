package ca.polymtl.inf8480.tp2.shared.exception;

import java.rmi.RemoteException;

public class OverloadingServerException extends RemoteException {
    public OverloadingServerException() {
        super("Operation cannot be executed by server at the moment.");
    }
}

