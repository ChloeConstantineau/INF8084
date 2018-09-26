package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileSystemInterface extends Remote, ServerInterface {
    //**File server**//
    boolean create(String name) throws RemoteException;
    //List
    String list() throws RemoteException;
    //SyncLocalDirectory
    String get(String name, String checksum) throws RemoteException;
    //Lock
    //Puh
}
