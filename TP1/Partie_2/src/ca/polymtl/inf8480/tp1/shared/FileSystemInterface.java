package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileSystemInterface extends Remote, ServerInterface {
    //**File server**//
    boolean create(Credentials credentials, String name) throws RemoteException;
    String list(Credentials credentials) throws RemoteException;
    //SyncLocalDirectory
    String get(Credentials credentials, String name, String checksum) throws RemoteException;
    boolean lock(Credentials credentials, Document file) throws RemoteException;
    boolean push(Credentials credentials, Document file) throws RemoteException;
}
