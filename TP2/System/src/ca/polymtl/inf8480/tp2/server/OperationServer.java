package ca.polymtl.inf8480.tp2.server;

import ca.polymtl.inf8480.tp2.shared.ServerDetails;
import ca.polymtl.inf8480.tp2.shared.*;
import ca.polymtl.inf8480.tp2.shared.exception.*;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class OperationServer implements IOperationServer {

    private String uniqueName = "";
    private OperationServerConfiguration configuration;
    private ILDAP LDAPStub;
    private ServerDetails LDAPconfiguration;

    public static void main(String[] args) {
        int serverId = Integer.parseInt(args[0]);
        if (args.length != 1 || serverId < 1 || serverId > 4) {
            System.out.println("Selection not handled.. Shutting down.");
            return;
        }

        OperationServer server;
        try {
            server = new OperationServer(serverId);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        server.run();
    }

    public OperationServer(int serverId) throws IOException {
        loadConfigurations(serverId);
        loadLDAPStub(LDAPconfiguration.host);
    }

    private void loadLDAPStub(String hostname) {
        System.out.println("Loading LDAP stub");
        try {
            Registry registry = LocateRegistry.getRegistry(Constants.RMI_REGISTRY_PORT);
            LDAPStub = (ILDAP) registry.lookup("LDAP");
        } catch (NotBoundException e) {
            System.out.println(ConsoleOutput.REGISTRY_NOT_FOUND.toString() + " : " + "LDAP");
        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void loadConfigurations(int id) throws IOException {
        this.configuration =
                Parser.<OperationServerConfiguration>parseJson(String.format(Constants.DEFAULT_OPERATION_SERVER_CONFIGS +
                        "server_%d.json", id), OperationServerConfiguration.class);
        LDAPconfiguration = Parser.loadLDAPDetails();
    }

    public void run() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        print(this.configuration.host);
        print(Integer.toString(this.configuration.port));

        IOperationServer stub = null;
        try {
            stub = (IOperationServer) UnicastRemoteObject.exportObject(this, this.configuration.port);

            Registry registry = LocateRegistry.getRegistry(LDAPconfiguration.host, Constants.RMI_REGISTRY_PORT);

            uniqueName = String.format("server_%d", this.configuration.port);
            registry.rebind(uniqueName, stub);
            System.out.println("OperationServer" + uniqueName + " ready.");

            // register server with LDAP
            LDAPStub.registerOperationServer(uniqueName);
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
    public int getCapacity() {
        return this.configuration.C;
    }

    @Override
    public String ping() {
        return "Pong!";
    }

    private boolean accept(int taskOperations) {
        int capacity = configuration.C;
        /* Every opServer will accept a taskNb <= capacity */
        if (taskOperations <= capacity) {
            return true;
        }

        /* Reject rate simulation */
        float rejectionThreshold = ((float) taskOperations - (float) capacity) / (4 * (float) capacity);
        float localValue = new Random().nextFloat();

        return localValue > rejectionThreshold;
    }

    private boolean isTrustworthy() {
        float localValue = new Random().nextFloat();
        return localValue > configuration.m;
    }

    @Override
    public TaskResult execute(Credentials credentials, Task task) throws RemoteException {
        // check if dispatcher is valid 10% of the time
        float value = new Random().nextFloat();
        if (value <= 0.1) {
            try {
                if (!LDAPStub.authenticateDispatcher(credentials))
                    return null;
            } catch (RemoteException e) {
                print(e.getMessage());
            }
        }

        // check if server accepts task
        if (!accept(task.operations.size())) {
            return TaskResult.of(0, new OverloadingServerException());
        }

        // Check if server is evil
        return isTrustworthy() ? trustedResponse(task) : untrustedResponse();
    }

    private TaskResult trustedResponse(Task task) {
        int result = getResult(task);
        System.out.println(result);
        return TaskResult.of(result, null);
    }

    private int getResult(Task task) {
        int result = 0;

        for (Operation op : task.operations) {
            switch (op.type) {
                case Pell:
                    result += Utils.pell(op.value);
                    break;
                case Prime:
                    result += Utils.prime(op.value);
                    break;
            }
            result = result % 4000;
        }
        return result;
    }

    private TaskResult untrustedResponse() {
        int fakeValue = new Random().nextInt(Integer.MAX_VALUE);
        System.out.println("FAKE: " + fakeValue);
        return TaskResult.of(fakeValue, null);
    }

    private static void print(String s) {
        System.out.println(s);
    }
}
