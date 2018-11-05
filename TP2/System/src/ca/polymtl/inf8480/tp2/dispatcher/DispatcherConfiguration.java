package ca.polymtl.inf8480.tp2.dispatcher;

import java.util.ArrayList;

public class DispatcherConfiguration {

    public boolean secureMode;
    public String fileName;
    public ArrayList<ServerDetails> availableServers;

    public DispatcherConfiguration() {
        availableServers = new ArrayList<>();
    }
}
