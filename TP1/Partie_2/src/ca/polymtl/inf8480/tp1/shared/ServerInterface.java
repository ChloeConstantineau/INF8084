package ca.polymtl.inf8480.tp1.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
	int execute(int a, int b) throws RemoteException;
	//**Auth**//
	boolean newClient(Credentials credentials) throws RemoteException;
	boolean verifyClient(Credentials credentials) throws RemoteException;
	//verify

	//**File server**//
	//Create
	//List
	//SyncLocalDirectory
	//Get
	//Lock
	//Puh
}
