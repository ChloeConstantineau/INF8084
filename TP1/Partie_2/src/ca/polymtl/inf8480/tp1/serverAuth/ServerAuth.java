package ca.polymtl.inf8480.tp1.serverAuth;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;

public class ServerAuth implements ServerInterface {

	public static void main(String[] args) {
		ServerAuth server = new ServerAuth();
		server.run();
	}

	public ServerAuth() {
		super();
	}

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("server", stub);
			System.out.println("ServerAuth ready.");
		} catch (ConnectException e) {
			System.err
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}

        // TODO : when starting server, read from file for login/password; add to hashmap
		try{
			newClient("r", "fr");
		} catch (RemoteException e){
			System.err.println("Erreur: " + e.getMessage());
		}
	}

    private void writeToFile(String username, String password) throws IOException {
	    String file = "./ServerSide/ClientList.txt";
	    String str = username + "@" + password;
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        writer.append(str + '\n');
        writer.close();
    }

	/*
	 * Méthode accessible par RMI. Additionne les deux nombres passés en
	 * paramètre.
	 */
	@Override
	public int execute(int a, int b) throws RemoteException {
		return a + b;
	}

	public boolean newClient(String login, String password) throws RemoteException {
        // client already exists ? return error message + false : add to registry(file and hashmap) + return true;


	    return true;
    }
}
