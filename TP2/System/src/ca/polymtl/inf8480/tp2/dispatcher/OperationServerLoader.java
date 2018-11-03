package ca.polymtl.inf8480.tp2.dispatcher;

import ca.polymtl.inf8480.tp2.shared.IOperationServer;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class OperationServerLoader {

    public static IOperationServer loadDistributor(String hostname) throws Exception {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        IOperationServer stub = null;

        try {
            Registry registry = hostname == "" ? LocateRegistry.getRegistry() : LocateRegistry.getRegistry(hostname, 5000);
            stub = (IOperationServer) registry.lookup("distributor");
        } catch (RemoteException e) {
            System.out.println("Error: the given name '" + e.getMessage()
                    + "' is not defined in the registry");
        } catch (NotBoundException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return stub;
    }
}