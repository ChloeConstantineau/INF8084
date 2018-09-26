package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthenticationInterface extends Remote {
	//**Auth**//
	boolean newClient(Credentials credentials) throws RemoteException;
	boolean verifyClient(Credentials credentials) throws RemoteException;
}
