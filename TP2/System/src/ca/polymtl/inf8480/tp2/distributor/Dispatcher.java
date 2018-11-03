package ca.polymtl.inf8480.tp2.distributor;

public class Dispatcher {

    WorkerLoader workerLoader = new WorkerLoader();

    static int countCheck = 0;
    static int secureMode = 1;
    int unsecureMode = 2;

    public Dispatcher(String s){

    }

    public static void main(String[] args) throws Exception{

        if (args.length != 2) {
            throw new Exception("Must receive 2 arguments, received: " + args.length );
        }

        String operationsPath = args[0];

        int isSecure = Integer.parseInt(args[1]);
        if(isSecure == 1)
        {
            countCheck = secureMode;
            System.out.println("Secure mode activated, number of checks: " + countCheck);
        }
        else{
            System.out.println("Unsercure mode activated, number of checks: " + countCheck);
        }

        Dispatcher dispatcher = new Dispatcher(operationsPath);

        long start = System.nanoTime();
        dispatcher.dispatch("this");
        long elapsedTime = System.nanoTime() - start;
        System.out.println("Elapsed time: " + elapsedTime/1000000 + " ms");
    }

    public void loadWorkers(String operationsPath){

    }

    public void dispatch(String operationsPath){

    }
}