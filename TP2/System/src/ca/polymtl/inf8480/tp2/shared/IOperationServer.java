package ca.polymtl.inf8480.tp2.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IOperationServer extends Remote {
    String ping() throws RemoteException;
    TaskResponse execute(Credentials credentials, Task task) throws RemoteException;
}
