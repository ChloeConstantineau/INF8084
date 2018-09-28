package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileSystemInterface extends Remote, ServerInterface {
    //**File server**//
    boolean create(String name) throws RemoteException;
    String list() throws RemoteException;
    //SyncLocalDirectory
    String get(String name, String checksum) throws RemoteException;
    boolean lock(Document file) throws RemoteException;
    boolean push(Document file) throws RemoteException;
}
