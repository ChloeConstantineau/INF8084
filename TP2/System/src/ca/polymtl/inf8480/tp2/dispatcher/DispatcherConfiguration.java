package ca.polymtl.inf8480.tp2.dispatcher;

import java.util.ArrayList;

public class DispatcherConfiguration {

    public int countCheck;
    public boolean secureMode;
    public String fileName;
    public ArrayList<String> availableServers;

    public DispatcherConfiguration(){
        availableServers = new ArrayList<>();
    }
}
