package ca.polymtl.inf8480.tp2.dispatcher;

import java.rmi.RemoteException;

public class SecureDispatcher extends Dispatcher {

    @Override
    public void process() {
        this.operationServers.forEach((k, v) -> {
            try {
                String response = v.ping();
                System.out.println(response);
            } catch (RemoteException e) {
                System.out.println(e.getMessage());
            }
        });
    }
}
