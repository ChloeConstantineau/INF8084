package ca.polymtl.inf8480.tp2.shared.exception;

import java.rmi.RemoteException;

public class ServerNotFoundException extends RemoteException {
    public ServerNotFoundException(){
        super("Given host name or port number could not be found");
    }
}
