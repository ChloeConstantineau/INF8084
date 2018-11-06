package ca.polymtl.inf8480.tp2.dispatcher;

import ca.polymtl.inf8480.tp2.shared.*;

import java.io.IOException;

public class Client {

    public static void main(String[] args) {
        DispatcherConfiguration config = null;

        try {
            config = loadDispatcherConfiguration(Constants.DEFAULT_DISPATCHER_CONFIGS);
            if (config != null) {
                System.out.println("Configurations are : ");
                System.out.println("File : " + config.fileName);
                System.out.println("SecureMode : " + config.secureMode);
                System.out.println("Number of servers : " + config.availableServers.size());
                System.out.println("Capacity factor is : " + config.capacityFactor);
            }

        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        } finally {
            if (config == null) {
                System.out.println("Unable to retrieve dispatcher configurations.. Shutting down.");
                return;
            }
        }

        Dispatcher dispatcher = config.secureMode ? new SecureDispatcher() : new UnsecureDispatcher();

        if (dispatcher != null) {
            try {
                dispatcher.initialize(config);
            } catch (Exception ioe) {
                System.out.println(ioe.getMessage());
            }
        }

        long start = System.nanoTime();
        dispatcher.dispatch();
        long elapsedTime = System.nanoTime() - start;
        System.out.println("Elapsed time: " + elapsedTime / 1000000 + " ms");
    }

    private static DispatcherConfiguration loadDispatcherConfiguration(String filename) throws IOException {
        return Parser.<DispatcherConfiguration>parseJson(filename, DispatcherConfiguration.class);
    }
}
