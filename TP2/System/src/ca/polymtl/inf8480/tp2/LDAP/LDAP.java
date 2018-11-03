package ca.polymtl.inf8480.tp2.LDAP;

import ca.polymtl.inf8480.tp2.shared.*;
import ca.polymtl.inf8480.tp2.shared.exception.ServerRegistrationException;

import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LDAP implements ILDAP {

    ConcurrentHashMap<String, InetAddress> operationServerRegistry = new ConcurrentHashMap<String, InetAddress>();

    public LDAP(){ }

    @Override
    public boolean authenticateDispatcher(Credentials credentials) throws RemoteException {
        return null;
    }

    @Override
    public ConcurrentHashMap<String, InetAddress> getAvailableOperationServer() throws RemoteException {

    }

    @Override
    public void registerOperationServer(String name, InetAddress address) throws RemoteException {
        try{
            operationServerRegistry.put(name, address);
        }catch(NullPointerException e){
            System.out.println(e.getMessage());
            throw new ServerRegistrationException();
        }
    }

    public boolean ping(ServerDetails serverDetails){
        try {
            Registry registry = LocateRegistry.getRegistry(serverDetails.host, Constants.RMI_REGISTRY_PORT);
            IOperationServer stub = ((IOperationServer) registry.lookup(serverDetails.host));
            stub.
        }
        catch(NotBoundException e){

        }
        catch(RemoteException e) {

        }
        catch (ServerNotActiveException sne) {
            log.error("Server not active, returning false.");
            return false;
        }
        return true;
    }


}
