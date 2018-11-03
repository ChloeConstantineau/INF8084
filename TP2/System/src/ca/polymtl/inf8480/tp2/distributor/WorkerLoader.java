package ca.polymtl.inf8480.tp2.distributor;

import ca.polymtl.inf8480.tp2.shared.IWorker;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class WorkerLoader {

    public static IWorker loadDistributor(String hostname) throws Exception {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        IWorker stub = null;

        try {
            Registry registry = hostname == "" ? LocateRegistry.getRegistry() : LocateRegistry.getRegistry(hostname, 5000);
            stub = (IWorker) registry.lookup("distributor");
        } catch (RemoteException e) {
            System.out.println("Error: the given name '" + e.getMessage()
                    + "' is not defined in the registry");
        } catch (NotBoundException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return stub;
    }
}