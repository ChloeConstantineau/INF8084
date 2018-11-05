package ca.polymtl.inf8480.tp2.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IOperationServer extends Remote {

    int getCapacity() throws RemoteException;

    String ping() throws RemoteException;

    TaskResult execute(Credentials credentials, Task task) throws RemoteException;
}
