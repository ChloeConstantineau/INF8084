package ca.polymtl.inf8480.tp1.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.lang.*;

import ca.polymtl.inf8480.tp1.shared.ServerInterface;

public class Client {
	
	public static void main(String[] args) {
		String distantHostname = "132.207.12.109";
		String action = null;

		if(args.length > 0){
			action = args[0];
		}
		else {
			System.out.println("Aucune commande recue");
		}

		Client client = new Client(distantHostname);


		switch(action){
			case "list":
				System.out.println("Get that list");
				// request list of file to server + display
				getThatList();
				break;

			default:
				break;
		}
		//client.run();
	}

	FakeServer localServer = null; // Pour tester la latence d'un appel de fonction normal.
	private ServerInterface localServerStub = null;
	private ServerInterface distantServerStub = null;

	public Client(String distantServerHostname) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		localServer = new FakeServer();
		localServerStub = loadServerStub("127.0.0.1");

		if (distantServerHostname != null) {
			distantServerStub = loadServerStub(distantServerHostname);
		}
	}

	private void run() {
		appelNormal();

		if (localServerStub != null) {
			appelRMILocal();
		}

		if (distantServerStub != null) {
			appelRMIDistant();
		}
	}

	private void getThatList() {
		localServer()
	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom '" + e.getMessage()
					+ "' n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}

	private void appelNormal() {
		// long start = System.nanoTime();
		// //int result = localServer.execute(4, 7);
		// byte[] byteArray = new byte[(int)Math.pow((double)10, (double)x_size)];
		// System.out.println("Payload size is " + byteArray.length);

		// localServer.emptyFunc(byteArray, byteArray);
	
		// long end = System.nanoTime();

		// System.out.println("Temps écoulé appel normal: " + (end - start) + " ns");
		
		//System.out.println("Résultat appel normal: " + result);
	}

	private void appelRMILocal() {
		
		// long start = System.nanoTime();
		// //int result = localServerStub.execute(4, 7);
		// byte[] byteArray = new byte[(int)Math.pow((double)10, (double)x_size)];
		// System.out.println("Payload size is " + byteArray.length);
		
		// try{
		// 	localServerStub.emptyFunc(byteArray, byteArray);
		// } catch(RemoteException e) {
		// 	System.out.println("emptyFunc threw and exception");
		// }
		// long end = System.nanoTime();

		// System.out.println("Temps écoulé appel RMI local: " + (end - start) + " ns");
		// //System.out.println("Résultat appel RMI local: " + result);
		
	}

	private void appelRMIDistant() {
		
		// long start = System.nanoTime();
		// //int result = distantServerStub.execute(4, 7);
		// byte[] byteArray = new byte[(int)Math.pow((double)10, (double)x_size)];
		// System.out.println("Payload size is " + byteArray.length);
		
		// try{
		// 	distantServerStub.emptyFunc(byteArray, byteArray);
		// } catch(RemoteException e) {
		// 	System.out.println("emptyFunc threw and exception");
		// }
		// long end = System.nanoTime();

		// System.out.println("Temps écoulé appel RMI distant: " + (end - start) + " ns");
		// //System.out.println("Résultat appel RMI distant: " + result);
		
	}
}
