package ca.polymtl.inf8480.tp1.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;

public class Client {

	public static String functionName;
	public static String[] param;


	public static void main(String[] args) {
		String localHostname = "127.0.0.1";
		functionName = "";
		param = new String[2];
		if (args.length >= 1) {
			functionName = args[0];
		}
		if (args.length >= 2) {
			param[0] = args[1];
		}
		if(args.length == 3){
			param[1] = args[2];
		}

		Client client = new Client(localHostname);
		client.run();
	}

	private ServerInterface localServerStub;


	public Client(String serverHostname) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		localServerStub = loadServerStub(serverHostname);
	}

	private void run() {
		if(localServerStub != null) {
			executeCall();
		}
	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}

	private void executeCall(){

		switch(functionName){
			case "new": {
//				if(param[0] == )
			}
			case "verify":
			case "create":
			case "list":
			case "syncLocalDirectory":
			case "get":
			case "lock":
		}
	}

	private void newClient(String login, String password){

	}

	private void VerifyClient(String login, String password){

	}
}
