package ca.polymtl.inf8480.tp1.client;

import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.nio.file.Files;

import ca.polymtl.inf8480.tp1.shared.ConsoleOutput;
import ca.polymtl.inf8480.tp1.shared.ServerInterface;
import ca.polymtl.inf8480.tp1.shared.Credentials;

public class Client {

    private static String functionName;
    private static String[] param;
    private ServerInterface AuthServerStub;
    private ServerInterface FileSystemStub;
    private String PathClientFiles = "ClientSide/Files";
    private String PathClientList = "ClientSide/ClientList.txt";
    private Credentials credentials;

    public static void main(String[] args) {
        String localHostname = "127.0.0.1";
        functionName = "";
        param = new String[2];
        Arrays.fill(param, "");

        if (args.length >= 1) {
            functionName = args[0];
        }
        if (args.length >= 2) {
            param[0] = args[1];
        }
        if (args.length == 3) {
            param[1] = args[2];
        }

        Client client = new Client(localHostname);
        client.run();
    }

    public Client(String serverHostname) {
        super();

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        AuthServerStub = loadServerStub(serverHostname, "authServer");
        FileSystemStub = loadServerStub(serverHostname, "fileSystemServer");
    }

    private void run() {
        if (AuthServerStub != null && FileSystemStub != null) {
            executeCall();
        }
    }

    private ServerInterface loadServerStub(String hostname, String registryName) {
        ServerInterface stub = null;

        try {
            Registry registry = LocateRegistry.getRegistry(hostname);
            stub = (ServerInterface) registry.lookup(registryName);
        } catch (NotBoundException e) {
            System.out.println(ConsoleOutput.REGISTRY_NOT_FOUND);
        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return stub;
    }

    private void executeCall() {

        switch (functionName) {
            case "new":
                identifyClient(param[0], param[1], true);
            case "verify":
                identifyClient(param[0], param[1], false);
            case "create":
                create(param[0]);
            case "list":
                list();
            case "syncLocalDirectory":
                syncLocalDirectory();
            case "get":
                get(param[0]);
            case "lock":
                lock(param[0]);
            case "push":
                push(param[0]);
        }
    }

    private void identifyClient(String username, String password, boolean isNewClient) {
        if (username == "" || password == "") {
            System.out.println(ConsoleOutput.INVALID_FUNCTION_CALL);
            return;
        }

        Credentials loginAttempt = new Credentials(username, password);

        try {
            boolean isAuthSuccessful = isNewClient ? AuthServerStub.newClient(loginAttempt) : AuthServerStub.verifyClient(loginAttempt);

            if (isAuthSuccessful) {
                this.credentials = loginAttempt;

                if (isNewClient) {
                    writeToClientList(credentials);
                }
                System.out.println(ConsoleOutput.REGISTRATION_APPROVED);

            } else {
                System.out.println(ConsoleOutput.REGISTRATION_DENIED);
            }

        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void create(String name) {

    }

    private void list() {

    }

    private void syncLocalDirectory() {

    }

    private void get(String name) {

    }

    private void lock(String name) {

    }

    private void push(String name) {

    }

    private void writeToClientList(Credentials credentials) {
        String text = credentials.username + ", " + credentials.password;
        try {
            Files.write(Paths.get(PathClientList), text.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
