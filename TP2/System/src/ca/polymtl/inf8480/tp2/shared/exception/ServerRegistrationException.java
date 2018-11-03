package ca.polymtl.inf8480.tp2.shared.exception;

import java.rmi.RemoteException;

public class ServerRegistrationException extends RemoteException {
    public ServerRegistrationException() {
        super("Operation server was not able to register with LDAP server");
    }
}
