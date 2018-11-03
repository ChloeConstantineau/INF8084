package ca.polymtl.inf8480.tp2.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface ILDAP extends Remote {

    void registerDispatcher(Credentials credentials) throws RemoteException;
    boolean authenticateDispatcher(Credentials credentials) throws RemoteException;
    ConcurrentLinkedQueue<String> getAvailableOperationServer() throws RemoteException;
    void registerOperationServer(String hostname) throws RemoteException;
}
