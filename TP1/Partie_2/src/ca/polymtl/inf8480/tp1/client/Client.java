package ca.polymtl.inf8480.tp1.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;

import ca.polymtl.inf8480.tp1.shared.ConsoleOutput;
import ca.polymtl.inf8480.tp1.shared.ServerInterface;

public class Client {

	public static String functionName;
	public static String[] param;


	public static void main(String[] args) {
		String localHostname = "127.0.0.1";
		functionName = "";
		param = new String[2];
		Arrays.fill(param, "");

		if (args.length >= 1) {
			functionName = args[0];
		}
		if (args.length >= 2) {
			param[0] = args[1];
		}
		if(args.length == 3){
			param[1] = args[2];
		}

		Client client = new Client(localHostname);
		client.run();
	}

	private ServerInterface AuthServerStub;
	private ServerInterface FileSystemStub;


	public Client(String serverHostname) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		AuthServerStub = loadServerStub(serverHostname, "authServer");
		FileSystemStub = loadServerStub(serverHostname, "fileSystemServer");
	}

	private void run() {
		if(AuthServerStub != null && FileSystemStub != null) {
			executeCall();
		}
	}

	private ServerInterface loadServerStub(String hostname, String registryName) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup(registryName);
		} catch (NotBoundException e) {
			System.out.println(ConsoleOutput.REGISTRY_NOT_FOUND);
		} catch (AccessException e) {
			System.out.println("Error: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Error: " + e.getMessage());
		}

		return stub;
	}

	private void executeCall(){

		switch(functionName){
			case "new": {
				if(param[0] == "" || param[1] == "")
					System.out.println(ConsoleOutput.INVALID_FUNCTION_CALL);
				else
					newClient(param[0], param[1]);
			}
			case "verify":{
				if(param[0] == "" || param[1] == "")
					System.out.println(ConsoleOutput.INVALID_FUNCTION_CALL);
				else
					verifyClient(param[0], param[1]);
			}
			case "create":
			{
				if(param[0] == "")
					System.out.println(ConsoleOutput.INVALID_FUNCTION_CALL);
				else
					create(param[0]);
			}
			case "list": list();
			case "syncLocalDirectory": syncLocalDirectory();
			case "get":
				{
				if(param[0] == "")
					System.out.println(ConsoleOutput.INVALID_FUNCTION_CALL);
				else
					get(param[0]);
			}
			case "lock":
				{
				if(param[0] == "")
					System.out.println(ConsoleOutput.INVALID_FUNCTION_CALL);
				else
					lock(param[0]);
			}
			case "push":
			{
				if(param[0] == "")
					System.out.println(ConsoleOutput.INVALID_FUNCTION_CALL);
				else
					push(param[0]);
			}
		}
	}

	private void newClient(String login, String password){

	}

	private void verifyClient(String login, String password){

	}

	private void create(String name){

	}

	private void list(){

	}

	private void syncLocalDirectory(){

	}

	private void get(String name){

	}

	private void lock(String name){

	}

	private void push(String name){

	}
}
