package ca.polymtl.inf8480.tp1.client;

import java.io.*;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.nio.file.Files;
import java.util.List;

import ca.polymtl.inf8480.tp1.shared.*;


public class Client {

    private static String functionName;
    private static String[] param;
    private AuthenticationInterface AuthServerStub;
    private FileSystemInterface FileSystemStub;
    private String PathClientFiles = "ClientSide/Files/";
    private String PathClientList = "ClientSide/ClientList.txt";
    private Credentials credentials;

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
        if (args.length == 3) {
            param[1] = args[2];
        }

        Client client = new Client(localHostname);
        client.run();
    }

    public Client(String serverHostname) {
        super();

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        AuthServerStub = (AuthenticationInterface) loadServerStub(serverHostname, "serverAuth");
        FileSystemStub = (FileSystemInterface) loadServerStub(serverHostname, "serverFileSystem");
    }

    private void run() {
        if (AuthServerStub != null) {
            executeCall();
        }
    }

    private ServerInterface loadServerStub(String hostname, String registryName) {
        ServerInterface stub = null;

        try {
            Registry registry = LocateRegistry.getRegistry(hostname);
            stub = (ServerInterface) registry.lookup(registryName);
        } catch (NotBoundException e) {
            System.out.println(ConsoleOutput.REGISTRY_NOT_FOUND.toString());
        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return stub;
    }

    private void executeCall() {

        switch (functionName) {
            case "new":
                identifyClient(param[0], param[1], true);
                break;
            case "verify":
                identifyClient(param[0], param[1], false);
                break;
            case "create":
                create(param[1]);
                break;
            case "list":
                list();
                break;
            case "syncLocalDirectory":
                syncLocalDirectory();
                break;
            case "get":
                get(param[0]);
                break;
            case "lock":
                lock(param[0]);
                break;
            case "push":
                push(param[0]);
                break;
        }
    }

    private void identifyClient(String username, String password, boolean isNewClient) {
        if (username == "" || password == "") {
            System.out.println(ConsoleOutput.INVALID_FUNCTION_CALL.toString());
            return;
        }

        Credentials loginAttempt = new Credentials(username, password);

        try {
            boolean isAuthSuccessful = isNewClient ? AuthServerStub.newClient(loginAttempt) : AuthServerStub.verifyClient(loginAttempt);

            if (isAuthSuccessful) {
                this.credentials = loginAttempt;

                if (isNewClient) {
                    writeToClientList(credentials);
                }
                System.out.println(ConsoleOutput.AUTH_APPROVED.toString());

            } else {
                System.out.println(ConsoleOutput.AUTH_DENIED.toString());
            }

        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void create(String name) {
        if (name == "") {
            System.out.println(ConsoleOutput.INVALID_FILE_NAME.toString());
            return;
        }

        try {
            if (FileSystemStub.create(name)) {
                System.out.println(ConsoleOutput.NEW_FILE_CREATED.toString() + " " + name);
            }
        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String list() {
        try {
            String fileList = FileSystemStub.list();
            System.out.println(fileList);

            return fileList;
        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    private void syncLocalDirectory() {

//        File folder = new File(PathClientFiles);
//        File[] listOfFiles = folder.listFiles();
//
//        for (int i = 0; i < listOfFiles.length; i++)
//            if (listOfFiles[i].isFile()) {
//                this.get(listOfFiles[i].getName());
//            }
    }

    private void get(String name) {
        if (name == "") {
            System.out.println(ConsoleOutput.INVALID_FILE_NAME.toString());
            return;
        }

        File f = new File(PathClientFiles + "/" + name);
        boolean isAlreadyInDirectory = f.exists();
        String fileContent = "";
        String checksum = null;

        if (isAlreadyInDirectory) {
            checksum = computeChecksum(name);
        } else {
            try {
                f.createNewFile();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        try {
            fileContent = FileSystemStub.get(name, checksum);

        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }

        if (fileContent == null && isAlreadyInDirectory)
            System.out.println(ConsoleOutput.CONTENT_IS_ALREADY_UP_TO_DATE.toString());
        else {
            try {
                PrintWriter writer = new PrintWriter(f);
                writer.print(fileContent);
                System.out.println(ConsoleOutput.CONTENT_UPDATED.toString());
            } catch (FileNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void lock(String name) {
        if (name == "") {
            System.out.println(ConsoleOutput.INVALID_FILE_NAME.toString());
            return;
        }

        //Is file stored server side
        String filesOnServer = list();
        if (!filesOnServer.contains(name)) {
            System.out.println(ConsoleOutput.FILE_404.toString());
        }

        //Is file stored client side
        File f = new File(PathClientFiles + "/" + name);
        String checksum = null;
        String content = getClientFileContent(name);
        if (!f.exists()) {
            try {
                FileSystemStub.lock(new Document(name, null, content));
            } catch (RemoteException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            checksum = computeChecksum(name);
        }
    }

    private void push(String name) {
        if (name == "") {
            System.out.println(ConsoleOutput.INVALID_FILE_NAME.toString());
            return;
        }

        boolean hasBeenPushed = false;
        String content = getClientFileContent(name);

        try {
            hasBeenPushed = FileSystemStub.push(new Document(name, null, content));
        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }

        ConsoleOutput output = hasBeenPushed ? ConsoleOutput.PUSHED_ACCEPTED : ConsoleOutput.PUSHED_DENIED;
        System.out.println(output.toString());
    }

    private void writeToClientList(Credentials credentials) {
        String text = credentials.username + ", " + credentials.password;
        try {
            Files.write(Paths.get(PathClientList), text.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String getClientFileContent(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(PathClientFiles + "/" + fileName)));
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    private String computeChecksum(String fileName) {
        String fileContent = getClientFileContent(fileName);
        byte[] content = new byte[1024];
        MessageDigest md = null;

        try {
            content = fileContent.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (
                NoSuchAlgorithmException e) {
            System.out.println("Error: " + e.getMessage());
        }

        if (md != null) {
            byte[] digest = md.digest(content);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digest.length; ++i) {
                sb.append(Integer.toHexString((digest[i] & 0xFF) | 0x100), 1, 3);
            }

            return sb.toString();
        }

        return null;
    }
}
