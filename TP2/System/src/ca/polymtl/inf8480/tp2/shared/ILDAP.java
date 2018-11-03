package ca.polymtl.inf8480.tp2.shared;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public interface ILDAP {

    boolean authenticateDispatcher(Credentials credentials) throws RemoteException;
    ConcurrentLinkedQueue<String> getAvailableOperationServer() throws RemoteException;
    void registerOperationServer(String name) throws RemoteException;
}
