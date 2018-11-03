package ca.polymtl.inf8480.tp2.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IOperationServer extends Remote {
    void reset(int load) throws RemoteException;
    String ping() throws RemoteException;
}
