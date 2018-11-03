package ca.polymtl.inf8480.tp2.server;

import ca.polymtl.inf8480.tp2.shared.*;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class OperationServer implements IOperationServer {

    private int load;
    private float wrongResultRate;
    private int capacity;


    public static void main(String[] args){
        if(!isValid(args)){
            return;
        }

        OperationServer server;
        try {
            server = new OperationServer(Float.parseFloat(args[0]), Integer.parseInt(args[1]));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        server.run();
    }

    public OperationServer(float m, int c) throws IOException {
        super();
        wrongResultRate = m;
        capacity = c;

    }

    private static void print(String s){
        System.out.println(s);
    }

    public static boolean isValid(String[] args){
        // Check that evilness level has been given
        if(args.length != 2){
            print(ConsoleOutput.NOT_ENOUGH_ARGS.toString());
            return false;
        }

        // Check if could be a number
        float m;
        int c;
        try {
            m = Float.parseFloat(args[0]);
            c = Integer.parseInt(args[1]);
        } catch(NumberFormatException e) {
            print(ConsoleOutput.NAN.toString());
            return false;
        }

        // Check evilness value is between 0 and 1
        if(m > 1 || m < 0) {
            print(ConsoleOutput.WRONG_ARGS.toString());
            return false;
        }
        if(c < 0){
            print(ConsoleOutput.POSITIVE_NUMBER_ONLY.toString());
            return false;
        }

        return true;
    }

    public void run(){
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            IOperationServer stub = (IOperationServer) UnicastRemoteObject
                    .exportObject(this, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("server", stub);
            System.out.println("OperationServer ready.");
        } catch (ConnectException e) {
            System.err
                    .println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
            System.err.println();
            System.err.println("Erreur: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }

        isTrustworthy();
    }

    @Override
    public void reset(int load) throws RemoteException {
        this.load = load;
    }

    private boolean accept(int taskOperations) {
        /* Every opServer will accept a taskNb <= capacity */
        if(taskOperations <= capacity){
            return true;
        }

        /* Reject rate simulation */
        float rejectionThreshold = ((float)taskOperations - (float)capacity)/(4 * (float)capacity);
        float localValue = new Random().nextFloat();

        return localValue > rejectionThreshold;
    }

    private boolean isTrustworthy(){
        float localValue = new Random().nextFloat();
        return localValue > wrongResultRate;
    }

    public TaskResponse execute(Credentials credentials, Task task) throws OverloadingServerException {
        // check if user is valid
        // ask LDAP.authentify(Credentials)

        // check if server accepts task
        if(!accept(task.operations.size())){
            throw new OverloadingServerException();
        }

        // Check if server is evil
        return isTrustworthy() ? trustedResponse(task) : untrustedResponse();
    }

    private TaskResponse trustedResponse(Task task){
        int result = getResult(task);
        return TaskResponse.of(result, ConsoleOutput.RIGHT_RESULT.toString());
    }

    private int getResult(Task task) {
        int result = 0;


        // ( ( ( 29 % 4000 + 5 ) % 4000 + 13860 ) % 4000 + 13 ) % 4000 = 1907
        for(Operation op : task.operations){

        }

        return result;
    }

    private TaskResponse untrustedResponse() {
        int fakeValue = new Random().nextInt();
        return TaskResponse.of(fakeValue, ConsoleOutput.WRONG_RESULT.toString());
    }
}
