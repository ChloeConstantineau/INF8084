package ca.polymtl.inf8480.tp2.server;

import ca.polymtl.inf8480.tp2.shared.ConsoleOutput;
import ca.polymtl.inf8480.tp2.shared.IOperationServer;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class OperationServer implements IOperationServer {

    private int load;
    private Float wrongResultRate;


    public static void main(String[] args){
        if(!isValid(args)){
            return;
        }

        OperationServer server;
        try {
            server = new OperationServer(Float.parseFloat(args[0]));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        server.run();
    }

    public OperationServer(Float m) throws IOException {
        super();
        wrongResultRate = m;

    }

    private static void print(String s){
        System.out.println(s);
    }

    public static boolean isValid(String[] args){
        // Check that evilness level has been given
        if(args.length != 1){
            print(ConsoleOutput.NOT_ENOUGH_ARGS.toString());
            return false;
        }

        // Check if could be a number
        Float m;
        try {
            m = Float.parseFloat(args[0]);
        } catch(NumberFormatException e) {
            print(ConsoleOutput.NAN.toString());
            return false;
        }

        // Check evilness value is between 0 and 1
        if(m > 1 || m < 0) {
            print(ConsoleOutput.WRONG_ARGS.toString());
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

    private boolean isAvailable() {
        return true;
    }

    private boolean isTrustworthy(){
        return true;
    }

//    public Collection<Operation> execute(){
    public void execute(){
        // check if user is valid

    }
}
