package ca.polymtl.inf8480.tp2.server;

import ca.polymtl.inf8480.tp2.shared.Constants;

public class OperationServerConfiguration {
    public int C;
    public float m;
    public int port;
    public String host;

    public OperationServerConfiguration(int capacity, int mischievous, int portNumber, String hostname){
        if (port < Constants.SERVER_MIN_PORT || port > Constants.SERVER_MAX_PORT) {
            throw new IllegalArgumentException("Port should be between 5000 and 5500");
        }

        this.C = capacity;
        this.m = mischievous;
        this.host = hostname;
        this.port = portNumber;
    }
}
