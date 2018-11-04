package ca.polymtl.inf8480.tp2.dispatcher;
import ca.polymtl.inf8480.tp2.shared.Constants;
import ca.polymtl.inf8480.tp2.shared.Parser;

import java.io.IOException;

public class Client {

    public static void main(String[] args){
        DispatcherConfiguration config = null;
        Dispatcher dispatcher = null;

        try{
            config = loadDispatcherConfiguration(Constants.DEFAULT_DISPATCHER_CONFIGS);
        }catch(IOException ioe){
            System.out.println(ioe.getMessage());
        }finally {
            if(config == null) {
                System.out.println("Unable to retreive dispatcher configurations.. Shutting down.");
                return;
            }
        }

        dispatcher = config.secureMode ? new SecureDispatcher() : new UnsecureDispatcher();


        long start = System.nanoTime();
        dispatcher.process();
        long elapsedTime = System.nanoTime() - start;
        System.out.println("Elapsed time: " + elapsedTime/1000000 + " ms");
    }

    private static DispatcherConfiguration loadDispatcherConfiguration(String filename) throws IOException {
        return Parser.<DispatcherConfiguration>parseJson(filename, DispatcherConfiguration.class);
    }
}
