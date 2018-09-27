package ca.polymtl.inf8480.tp1.serverFileSystem;

import java.io.*;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import ca.polymtl.inf8480.tp1.shared.Credentials;
import ca.polymtl.inf8480.tp1.shared.FileSystemInterface;

public class ServerFileSystem implements FileSystemInterface {

    public static void main(String[] args) {
        ServerFileSystem server = new ServerFileSystem();
        server.run();
    }

    public ServerFileSystem() {
        super();
    }

    private void run() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            FileSystemInterface stub = (FileSystemInterface) UnicastRemoteObject
                    .exportObject(this, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("serverFileSystem", stub);
            System.out.println("serverFileSystem ready.");
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
            // TODO : Something
        }
    }


    /*
     * Méthode accessible par RMI.
     */
    public boolean create(String name) throws RemoteException {
        return true;
    }

    public String list() throws RemoteException {
        return null;
    }

    public String get(String name, String checksum) throws RemoteException {
        return null;
    }
}
