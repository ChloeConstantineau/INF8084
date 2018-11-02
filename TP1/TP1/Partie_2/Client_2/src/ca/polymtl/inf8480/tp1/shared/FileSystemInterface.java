package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface FileSystemInterface extends Remote, ServerInterface {
    //**File server**//
    boolean create(Credentials credentials, String name) throws RemoteException;
    boolean push(Credentials credentials, Document file) throws RemoteException;

    String list(Credentials credentials) throws RemoteException;
    String get(Credentials credentials, String name, String checksum) throws RemoteException;

    Lock lock(Credentials credentials, String name) throws RemoteException;
    ArrayList<Document> syncLocalDirectory(Credentials credentials) throws RemoteException;
}
