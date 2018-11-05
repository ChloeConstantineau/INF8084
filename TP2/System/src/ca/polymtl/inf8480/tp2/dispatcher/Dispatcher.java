package ca.polymtl.inf8480.tp2.dispatcher;

import ca.polymtl.inf8480.tp2.shared.*;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Dispatcher {

    protected DispatcherConfiguration configuration = null;
    protected ConcurrentLinkedQueue<Operation> pendingOperations = new ConcurrentLinkedQueue<Operation>();
    protected ConcurrentLinkedQueue<TaskResult> taskResults = new ConcurrentLinkedQueue<TaskResult>();
    protected int nbOperations = 0;
    protected ConcurrentHashMap<Integer, IOperationServer> operationServers = new ConcurrentHashMap<Integer, IOperationServer>();
    protected List<Integer> operationServerIds = new ArrayList<>();
    protected int finalResult = 0;

    private ILDAP LDAPstub;

    public Dispatcher() {
    }

    public final void initialize(DispatcherConfiguration configuration) throws IllegalArgumentException {
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration should not be null");
        }

        this.configuration = configuration;

        // get LDAP stub and register Dispatcher with LDAP
        LDAPstub = (ILDAP) loadLDAPInterface(Constants.HOSTNAME, "LDAP");
        try {
            LDAPstub.registerDispatcher(configuration.credentials);
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }

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

    private ILDAP loadLDAPInterface(String hostname, String registryName) {
        ILDAP stub = null;

        System.out.println("Loading LDAP stub");
        try {
            Registry registry = LocateRegistry.getRegistry(hostname);
            stub = (ILDAP) registry.lookup(registryName);
        } catch (NotBoundException e) {
            System.out.println(ConsoleOutput.REGISTRY_NOT_FOUND.toString() + " : " + registryName);
        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return stub;
    }

    private final void loadOperationStubs() {  //TODO : change for LDAP list
        if (this.configuration == null) {
            return;
        }

        System.out.println("Loading server stubs.");

        for (ServerDetails serverConfig : this.configuration.availableServers) {
            IOperationServer stub = this.loadServerStub(serverConfig);
            if (stub != null) {
                this.operationServers.put(serverConfig.port, stub);
                this.operationServerIds.add(serverConfig.port);
            }
        }
    }

    private IOperationServer loadServerStub(ServerDetails config) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        IOperationServer stub = null;

        try {
            Registry registry = LocateRegistry.getRegistry(config.host, Constants.RMI_REGISTRY_PORT);
            String specificName = String.format("server_%d", config.port);
            stub = (IOperationServer) registry.lookup(specificName);
        } catch (RemoteException e) {
            System.out.println("Error: the given name '" + e.getMessage()
                    + "' is not defined in the registry");
        } catch (NotBoundException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return stub;
    }

    private final void loadOperationsFromFile(String fileLocation) {
        System.out.println("Loading operations from file : " + fileLocation);
        Path filePath = Paths.get(fileLocation);

        if (!Files.exists(filePath)) {
            System.out.println("Could not find operations' file... ");
            return;
        }

        try {
            Charset cs = Charset.forName("utf-8");
            List<String> instructions = Files.readAllLines(filePath, cs);

            for (String instruction : instructions) {
                String[] instructionElements = instruction.split(" ");
                if (instructionElements.length != 2) {
                    continue;
                }

                String fct = instructionElements[0];
                int value = Integer.parseInt(instructionElements[1]);

                if (fct.equals(OperationType.Pell.toString())) {
                    this.pendingOperations.add(Operation.of(OperationType.Pell, value));
                } else if (fct.equals(OperationType.Prime.toString())) {
                    this.pendingOperations.add(Operation.of(OperationType.Prime, value));
                }
            }

        } catch (IOException e) {
            System.out.println("Unable to read from file..");
        }

        if (this.pendingOperations != null) {
            this.nbOperations = this.pendingOperations.size();
        }
    }

    protected void makeTaskEasier(){
        this.configuration.capacityFactor = this.configuration.capacityFactor > 0 ?
                this.configuration.capacityFactor-- :  this.configuration.capacityFactor;
    }

    protected void populatePendingOperations(List<Operation> operations){
        for (Operation op: operations) {
            this.pendingOperations.add(op);
        }
    }

    protected void setFinalResult(){

        for (TaskResult result : this.taskResults)
        {
            finalResult += result.result;
        }

        System.out.println(finalResult);
    }

    //To be overridden
    public abstract void dispatch();
}