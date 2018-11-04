package ca.polymtl.inf8480.tp2.server;

import ca.polymtl.inf8480.tp2.shared.*;
import ca.polymtl.inf8480.tp2.shared.exception.OverloadingServerException;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.rmi.RemoteException;
import java.util.Scanner;

public class OperationServer implements IOperationServer {

    private OperationServerConfiguration configuration;

    public static void main(String[] args) {
        Scanner reader = new Scanner(System.in);
        System.out.println(Constants.USER_MENU_OPARATIONS_SERVER);
        int serverId = reader.nextInt();
        reader.close();

        if (serverId < 1 || serverId > 4) {
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
        loadConfiguration(serverId);        
    }

    private void loadConfiguration(int id) throws IOException {
        this.configuration =
                Parser.<OperationServerConfiguration>parseJson(String.format(Constants.DEFAULT_OPERATIONSERVER_CONFIGS + "server_%d.json", id), OperationServerConfiguration.class);
    }

    private static void print(String s) {
        System.out.println(s);
    }

    public void run() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        System.out.println(this.configuration.host);
        System.out.println(this.configuration.port);

        IOperationServer stub = null;
        try {
            stub = (IOperationServer) UnicastRemoteObject
                    .exportObject(this, this.configuration.port);

            Registry registry = LocateRegistry.getRegistry(configuration.host, Constants.RMI_REGISTRY_PORT);
            
            String specificName = String.format("server_%d", this.configuration.port);
            registry.rebind(specificName, stub);
            System.out.println("OperationServer" + specificName + " ready.");
        } catch (ConnectException e) {
            System.err
                    .println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
            System.err.println();
            System.err.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        isTrustworthy();
    }

    @Override
    public String ping() throws RemoteException {
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
    public TaskResponse execute(Credentials credentials, Task task) throws OverloadingServerException {
        // check if user is valid
        // ask LDAP.authentify(Credentials)

        // check if server accepts task
        if (!accept(task.operations.size())) {
            throw new OverloadingServerException();
        }

        // Check if server is evil
        return isTrustworthy() ? trustedResponse(task) : untrustedResponse();
    }

    private TaskResponse trustedResponse(Task task) {
        int result = getResult(task);
        return TaskResponse.of(result, ConsoleOutput.RIGHT_RESULT.toString());
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

    private TaskResponse untrustedResponse() {
        int fakeValue = new Random().nextInt(Integer.MAX_VALUE);
        return TaskResponse.of(fakeValue, ConsoleOutput.WRONG_RESULT.toString());
    }
}
