package ca.polymtl.inf8480.tp1.serverAuth;

import java.io.*;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;

import ca.polymtl.inf8480.tp1.shared.*;

public class ServerAuth implements AuthenticationInterface {

    private HashMap<String, String> credentialMap = new HashMap<>();
    private FileSystemInterface fileSystemStub;
    private final String PATH = "./ServerSide/ClientList.txt";
    private final String SERVERHOST_NAME = "127.0.0.1";



    public static void main(String[] args) {
		ServerAuth server = new ServerAuth();
		server.run();
	}

	public ServerAuth() {
		super();

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        fileSystemStub = (FileSystemInterface) loadServerStub(SERVERHOST_NAME, "serverFileSystem");
	}

    private ServerInterface loadServerStub(String hostname, String registryName) {
        ServerInterface stub = null;

        try {
            Registry registry = LocateRegistry.getRegistry(hostname);
            stub = (ServerInterface) registry.lookup(registryName);
        } catch (NotBoundException e) {
            System.out.println(ConsoleOutput.REGISTRY_NOT_FOUND.toString());
        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return stub;
    }

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			AuthenticationInterface stub = (AuthenticationInterface) UnicastRemoteObject
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
	    String str = credentials.username + "@" + credentials.password;
	    BufferedWriter writer = new BufferedWriter(new FileWriter(PATH, true));

        writer.append(str + '\n');
        writer.close();
    }

    private void readFromFile() throws  IOException {
        File file = new File(PATH);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String str;
        while ((str = br.readLine()) != null){
            addCredientials(str);
        }
    }

    private void addCredientials(String s){
	    String username = s.substring(0, s.indexOf("@"));
	    String password = s.substring(s.indexOf("@") + 1, s.length());
	    credentialMap.put(username, password);
    }

    private void addCredentials(Credentials credentials){
        credentialMap.put(credentials.username, credentials.password);
        try {
            writeToFile(credentials);
        } catch (IOException e){
            System.err.println("Error: " + e.getMessage());
        }
    }


	/*
	 * Méthode accessible par RMI.
	 */
	public boolean newClient(Credentials credentials) throws RemoteException {
	    System.out.println("New client requested");

        if(credentialMap.containsKey(credentials.username)){
            System.out.println("Client already exists");
	        return false;
        }

        addCredentials(credentials);
        System.out.println("New client created");
	    return true;
    }

    public boolean verifyClient(Credentials credentials) throws RemoteException {
        System.out.println("New client verification requested");
        String name = credentials.username;
        String password = credentials.password;

        return credentialMap.containsKey(name) && credentialMap.get(name).equals(password);
    }

}
