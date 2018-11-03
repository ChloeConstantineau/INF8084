package ca.polymtl.inf8480.tp2.shared;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

public interface ILDAP {

    boolean authenticateDispatcher(Credentials credentials) throws RemoteException;
    ConcurrentHashMap<String, InetAddress> getAvailableOperationServer() throws RemoteException;
    void registerOperationServer(String name, InetAddress address) throws RemoteException;
}
