package ca.polymtl.inf8480.tp2.shared;

import ca.polymtl.inf8480.tp2.shared.exception.OverloadingServerException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IOperationServer extends Remote {
    int getCapacity();
    int getId();

    String ping() throws RemoteException;

    TaskResult execute(Credentials credentials, Task task) throws OverloadingServerException;
}
