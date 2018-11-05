package ca.polymtl.inf8480.tp2.dispatcher;

import ca.polymtl.inf8480.tp2.shared.Credentials;

import java.util.ArrayList;

public class DispatcherConfiguration {

    public Credentials credentials;
    public boolean secureMode;
    public String fileName;
    public ArrayList<ServerDetails> availableServers;
    public int capacityFactor;

    public DispatcherConfiguration() {
        credentials = Credentials.of("", "");
        availableServers = new ArrayList<>();
    }
}
