package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    //**Auth**//
    boolean newClient(Credentials credentials) throws RemoteException;

    boolean verifyClient(Credentials credentials) throws RemoteException;

    //**File server**//
    boolean create(String name) throws RemoteException;
    //List
    String list() throws RemoteException;
    //SyncLocalDirectory
    String get(String name, String checksum) throws RemoteException;
    //Lock
    //Puh
}
