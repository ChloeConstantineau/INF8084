package ca.polymtl.inf8480.tp2.dispatcher;

public class Client {

    public static void main(String[] args) throws Exception{

        if (args.length != 2) {
            throw new Exception("Must receive 2 arguments, received: " + args.length );
        }

        String operationsPath = args[0];

        int isSecure = Integer.parseInt(args[1]);
        if(isSecure == 1)
        {
            System.out.println("Secure mode activated, number of checks: ");
        }
        else{
            System.out.println("Unsecure mode activated, number of checks: ");
        }
        long start = System.nanoTime();
        long elapsedTime = System.nanoTime() - start;
        System.out.println("Elapsed time: " + elapsedTime/1000000 + " ms");
    }

}
