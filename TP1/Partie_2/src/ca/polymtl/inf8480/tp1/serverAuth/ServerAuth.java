package ca.polymtl.inf8480.tp1.serverAuth;

import java.io.*;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashSet;

import ca.polymtl.inf8480.tp1.shared.Credentials;
import ca.polymtl.inf8480.tp1.shared.ServerInterface;

public class ServerAuth implements ServerInterface {

    HashSet<Credentials> credentialsSet = new HashSet<>();

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
			registry.rebind("serverAuth", stub);
			System.out.println("ServerAuth ready.");
		} catch (ConnectException e) {
			System.err
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}

        // when starting server, read from file for login/password
        // add to hashSet
        try {
		    readFromFile();
        } catch (IOException e){
            System.err.println("Erreur: " + e.getMessage());
        }

	}

    private void writeToFile(Credentials credentials) throws IOException {
	    String path = "./ServerSide/ClientList.txt";
	    String str = credentials.username + "@" + credentials.password;
	    BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));

        writer.append(str + '\n');
        writer.close();
    }

    private void readFromFile() throws  IOException {
        String path = "./ServerSide/ClientList.txt";
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String str;
        while ((str = br.readLine()) != null){
            addCredientials(str);
        }
    }

    private void addCredientials(String s){
	    String username = s.substring(0, s.indexOf("@"));
	    String password = s.substring(s.indexOf("@") + 1, s.length());
	    credentialsSet.add(new Credentials(username, password));
    }

    private void addCredentials(Credentials credentials){
        credentialsSet.add(credentials);
        try {
            writeToFile(credentials);
        } catch (IOException e){
            System.err.println("Error: " + e.getMessage());
        }
    }


	/*
	 * Méthode accessible par RMI. Additionne les deux nombres passés en
	 * paramètre.
	 */
	public boolean newClient(Credentials credentials) throws RemoteException {
        
	    // check if is already registered
	    if(credentialsSet.contains(credentials)){
	        return false;
        }

        // if not, add to hashSet and to file
        addCredentials(credentials);
	    return true;
    }

    public boolean verifyClient(Credentials credentials) throws RemoteException {
	    return credentialsSet.contains(credentials);
    }

    // TODO : implement
    public boolean create(String name) throws RemoteException {
	    return true;
    }

    // TODO : implement
    public String list() throws RemoteException {
	    return "something";
    }

}
