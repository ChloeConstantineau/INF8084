package ca.polymtl.inf8480.tp2.dispatcher;

import ca.polymtl.inf8480.tp2.server.OperationServerConfiguration;

import java.util.ArrayList;

public class DispatcherConfiguration {

    public boolean secureMode;
    public String fileName;
    public ArrayList<OperationServerConfiguration> availableServers;

    public DispatcherConfiguration(){
        availableServers = new ArrayList<>();
    }
}
