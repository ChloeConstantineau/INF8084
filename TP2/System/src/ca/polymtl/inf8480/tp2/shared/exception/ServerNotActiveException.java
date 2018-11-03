package ca.polymtl.inf8480.tp2.shared.exception;

import java.rmi.RemoteException;

public class ServerNotActiveException extends RemoteException {
    public ServerNotActiveException() {
        super("Pinged server is not responding");
    }
}
