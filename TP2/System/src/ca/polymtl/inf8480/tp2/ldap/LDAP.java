package ca.polymtl.inf8480.tp2.ldap;

import ca.polymtl.inf8480.tp2.shared.*;
import ca.polymtl.inf8480.tp2.shared.exception.*;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class LDAP implements ILDAP {

    ConcurrentLinkedQueue<String> operationServerRegistry = new ConcurrentLinkedQueue<>();
    ConcurrentHashMap<String, Credentials> dispatcherRegistry = new ConcurrentHashMap<>();
    ServerDetails configs;

    public LDAP() {
        super();
        try{
            ServerDetails config = Parser.loadLDAPDetails();
            configs = new ServerDetails(config.host, config.port);

        }catch(IOException e){
            System.out.println("Unable to load LDAP config details");
        }
    }

    public static void main(String[] args) {
        LDAP server = new LDAP();
        server.run();
    }

    public void run() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            ILDAP stub = (ILDAP) UnicastRemoteObject.exportObject(this, Constants.LDAP_PORT);
            Registry registry = LocateRegistry.createRegistry(Constants.RMI_REGISTRY_PORT);
            registry.rebind("LDAP", stub);
            System.out.println("LDAP server ready.");
        } catch (ConnectException e) {
            System.err
                    .println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
            System.err.println();
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    @Override
    public boolean authenticateDispatcher(Credentials credentials) {
        System.out.println("Authenticating register");
        if (!dispatcherRegistry.containsKey(credentials.username)) {
            return false;
        }
        return dispatcherRegistry.get(credentials.username).password.equals(credentials.password);
    }

    public void registerDispatcher(Credentials credentials) throws RemoteException {
        System.out.println("Registering dispatcher");
        try {
            if (dispatcherRegistry.contains(credentials.username))
                throw new ServerRegistrationException();
            dispatcherRegistry.put(credentials.username, credentials);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            throw new ServerRegistrationException();
        }
    }

    @Override
    public ConcurrentLinkedQueue<String> getAvailableOperationServer() {
        ArrayList<String> deadServer = new ArrayList<>();

        for (String i : operationServerRegistry) {
            if (!ping(i))
                deadServer.add(i);
        }

        for (String s : deadServer) {
            operationServerRegistry.remove(s);
        }

        return operationServerRegistry;
    }

    @Override
    public void registerOperationServer(String hostname) throws RemoteException {
        System.out.println("Registering Operation Server");
        try {
            if (operationServerRegistry.contains(hostname))
                throw new ServerRegistrationException();
            operationServerRegistry.add(hostname);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            throw new ServerRegistrationException();
        }
    }

    public boolean ping(String hostname) {
        if (hostname == null) {
            throw new IllegalArgumentException();
        }

        try {
            Registry registry = LocateRegistry.getRegistry(hostname, Constants.RMI_REGISTRY_PORT);
            IOperationServer stub = ((IOperationServer) registry.lookup(hostname));
            stub.ping();

        } catch (NotBoundException e) {
            System.out.println(e.getMessage());

        } catch (RemoteException e) {
            return false;
        }
        return true;
    }
}
