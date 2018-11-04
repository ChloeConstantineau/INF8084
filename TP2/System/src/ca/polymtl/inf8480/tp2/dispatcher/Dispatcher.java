package ca.polymtl.inf8480.tp2.dispatcher;

import ca.polymtl.inf8480.tp2.shared.*;
import ca.polymtl.inf8480.tp2.shared.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Dispatcher {

    protected DispatcherConfiguration configuration = null;
    protected ConcurrentLinkedQueue<Operation> pendingOperations = new ConcurrentLinkedQueue<>();
    protected int nbOperations = 0;
    protected HashMap<String, IOperationServer> operationServers = new HashMap<>();

    public Dispatcher() {
    }

    public final void initialize(DispatcherConfiguration configuration) throws IllegalArgumentException {
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration should not be null");
        }

        this.configuration = configuration;

        this.loadOperationStubs();

        if (this.operationServers == null || this.operationServers.isEmpty()) {
            System.out.println("No operation servers available... Shutting down.");
            return;
        }

        String fileLocation = Constants.OPERATION_DATA_PATH + this.configuration.fileName;
        loadOperationsFromFile(fileLocation);

        if (this.pendingOperations == null || this.pendingOperations.isEmpty()) {
            System.out.println("No operations found...");
            return;
        }

    }

    private final void loadOperationStubs() {
        if (this.configuration == null) {
            return;
        }

        for (String serverHostname : this.configuration.availableServers) {
            IOperationServer stub = this.loadServerStub(serverHostname);
            if (stub != null) {
                this.operationServers.put(serverHostname, stub);
            }
        }
    }

    private IOperationServer loadServerStub(String hostname) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        IOperationServer stub = null;

        try {
            Registry registry = hostname == "" ? LocateRegistry.getRegistry() : LocateRegistry.getRegistry(hostname, Constants.RMI_REGISTRY_PORT);
            stub = (IOperationServer) registry.lookup("server");
        } catch (RemoteException e) {
            System.out.println("Error: the given name '" + e.getMessage()
                    + "' is not defined in the registry");
        } catch (NotBoundException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return stub;
    }

    private final void loadOperationsFromFile(String fileLocation) {
        try {
            File file = new File(fileLocation);
            BufferedReader br = new BufferedReader(new FileReader(file));

            String str;
            while ((str = br.readLine()) != null) {
                List<String> line = Arrays.asList(str.split(" "));

                if (line.size() != 2) {
                    throw new IOException();
                }

                String fct = line.get(0);
                int value = Integer.parseInt(line.get(1));

                if (fct == OperationType.Pell.toString() || fct == OperationType.Prime.toString())
                    pendingOperations.add(Operation.of(OperationType.Pell, value));

                if (!pendingOperations.isEmpty())
                    nbOperations = pendingOperations.size();
            }

        } catch (IOException ioe) {
            System.out.println("Unable to read operations' file...");
        }
    }

    //To be overridden
    public abstract void process();

}