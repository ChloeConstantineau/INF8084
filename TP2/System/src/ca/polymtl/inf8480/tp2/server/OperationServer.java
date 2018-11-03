package ca.polymtl.inf8480.tp2.server;

import ca.polymtl.inf8480.tp2.shared.ConsoleOutput;
import ca.polymtl.inf8480.tp2.shared.Credentials;
import ca.polymtl.inf8480.tp2.shared.IOperationServer;
import ca.polymtl.inf8480.tp2.shared.OverloadingServerException;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class OperationServer implements IOperationServer {

    private int load;
    private Float wrongResultRate;
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

    public OperationServer(Float m, int c) throws IOException {
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
        Float m;
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
    }

    @Override
    public void reset(int load) throws RemoteException {
        this.load = load;
    }

    private boolean acceptTask(int taskOperations) {
        /* Ainsi, chaque calculateur acceptera toutes les tâches contenant
         * au plus Ci opérations mathématiques
         */
        if(taskOperations <= capacity){
            return true;
        }

        /* On simulera le taux de refus des tâches à l'aide d'une
        * fonction mathématique simple
        */
        double treshold = (taskOperations - capacity)/(4 * capacity);
        Random r = new Random();
        double localValue = r.nextDouble();

        return localValue > treshold;
    }

    private boolean isTrustworthy(){
        return true;
    }

//    public Collection<Operation> execute(){
    // will be passing a collection of tasks probably
    public void execute(Credentials credentials, int numberOfTask) throws RemoteException{
        // check if user is valid

        // check if server accepts task
        if(!acceptTask(numberOfTask)){
            throw new OverloadingServerException();
        }



    }
}
