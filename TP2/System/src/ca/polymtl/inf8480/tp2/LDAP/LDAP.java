package ca.polymtl.inf8480.tp2.LDAP;

import ca.polymtl.inf8480.tp2.shared.*;
import ca.polymtl.inf8480.tp2.shared.exception.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class LDAP implements ILDAP {

    ConcurrentLinkedQueue<String> operationServerRegistry = new ConcurrentLinkedQueue<>();
    ConcurrentHashMap<String, Credentials> dispatcherRegistry = new ConcurrentHashMap<>();

    public LDAP() {
    }

    @Override
    public boolean authenticateDispatcher(Credentials credentials) {
        if(!dispatcherRegistry.containsKey(credentials.username)){
            return false;
        }
        return dispatcherRegistry.get(credentials.username).password == credentials.password;
    }

    public void registerDispatcher(Credentials credentials) throws RemoteException {
        try {
            if(dispatcherRegistry.contains(credentials.username))
                throw new ServerRegistrationException();
            dispatcherRegistry.put(credentials.username, credentials);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            throw new ServerRegistrationException();
        }
    }

    @Override
    public void registerOperationServer(String hostname) throws RemoteException {
        try {
            if(operationServerRegistry.contains(hostname))
                throw new ServerRegistrationException();
            operationServerRegistry.add(hostname);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            throw new ServerRegistrationException();
        }
    }

    @Override
    public ConcurrentLinkedQueue<String> getAvailableOperationServer() {
        for (String i: operationServerRegistry) {
            if(!ping(i))
                operationServerRegistry.remove(i);
        }
        return operationServerRegistry;
    }

    public boolean ping(String hostname){
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
