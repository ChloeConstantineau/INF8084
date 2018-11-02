package ca.polymtl.inf8480.tp1.client;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.nio.file.Files;

import ca.polymtl.inf8480.tp1.shared.*;

import javax.print.Doc;


public class Client {

    private static String functionName;
    private static String[] param;
    private AuthenticationInterface authServerStub;
    private FileSystemInterface fileSystemStub;
    private String pathClientFiles = "ClientSide/Files/";
    private String pathClientList = "ClientSide/ClientList.txt";
    private String pathCurrentUser = "ClientSide/CurrentUser.txt";

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
        authServerStub = (AuthenticationInterface) loadServerStub(serverHostname, "serverAuth");
        fileSystemStub = (FileSystemInterface) loadServerStub(serverHostname, "serverFileSystem");
    }

    private void run() {
        if (authServerStub != null || fileSystemStub != null) {
            executeCall();
        } else {
            System.out.println("Server stubs not instantiated");
        }
    }

    private ServerInterface loadServerStub(String hostname, String registryName) {
        ServerInterface stub = null;

        try {
            Registry registry = LocateRegistry.getRegistry(hostname);
            stub = (ServerInterface) registry.lookup(registryName);
        } catch (NotBoundException e) {
            System.out.println(ConsoleOutput.REGISTRY_NOT_FOUND.toString() + " : " + registryName);
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
                create(param[0]);
                break;
            case "list":
                list();
                break;
            case "syncLocalDirectory":
                syncLocal(true);
                break;
            case "syncLocalFiles":
                syncLocal(false);
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
            default:
                System.out.println(ConsoleOutput.INVALID_FUNCTION_CALL.toString());
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
            boolean isAuthSuccessful = isNewClient ? authServerStub.newClient(loginAttempt) : authServerStub.verifyClient(loginAttempt);

            if (isAuthSuccessful) {
                setCurrentUser(loginAttempt);
                if (isNewClient) {
                    writeToClientList(loginAttempt);
                    System.out.println(ConsoleOutput.REGISTRATION_APPROVED.toString());
                } else
                    System.out.println(ConsoleOutput.AUTH_APPROVED.toString());

            } else {
                if (isNewClient)
                    System.out.println(ConsoleOutput.REGISTRATION_DENIED.toString());
                else
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

        Credentials credentials = getCurrentUser();
        if(credentials == null){
            System.out.println(ConsoleOutput.INVALID_CREDENTIALS.toString());
            return;
        }

        try {
            if (fileSystemStub.create(credentials, name)) {
                Path file = Paths.get(pathClientFiles + name);
                List<String> s = Arrays.asList("");

                try {
                    Files.write(file, s , Charset.forName("UTF-8"));
                } catch (IOException e){
                    System.err.println("Error: " + e.getMessage());
                }

                System.out.println(ConsoleOutput.NEW_FILE_CREATED.toString() + " " + name);
            }
        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void list() {
        Credentials credentials = getCurrentUser();
        if(credentials == null){
            System.out.println(ConsoleOutput.INVALID_CREDENTIALS.toString());
        }

        try {
            String fileList = fileSystemStub.list(credentials);
            System.out.println(fileList);

        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private HashSet<String> getList() {
        Credentials credentials = getCurrentUser();
        if(credentials == null){
            return null;
        }

        try {
            String fileList = fileSystemStub.list(credentials);
            String[] files = fileList.split("\n");
            HashSet<String> hashSet = new HashSet<>();

            for(String s : files){
                int lockOwner = s.indexOf(", locked by");
                s = lockOwner > 0 ? s.substring(0, lockOwner) : s;
                hashSet.add(s);
            }

            return hashSet;
        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    private void syncLocal(boolean allFiles) {
        Credentials credentials = getCurrentUser();
        if(credentials == null){
            System.out.println(ConsoleOutput.INVALID_CREDENTIALS.toString());
        }

        ArrayList<Document> serverDocuments = new ArrayList<>();
        try {
            serverDocuments = fileSystemStub.syncLocalDirectory(credentials);
        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }

        Iterator<Document> itr = serverDocuments.iterator();
        while(itr.hasNext()){
            Document doc = itr.next();
            File file = new File(pathClientFiles + doc.name);
            if(file.exists()){
                try {
                    writeToFile(pathClientFiles + doc.name, doc.content);
                } catch (IOException e){
                    System.err.println(e.getMessage());
                }
            } else if(allFiles) {
                // create file in directory + write in it
                create(doc.name);
                try{
                    writeToFile(pathClientFiles + doc.name, doc.content);
                } catch (IOException e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }
        System.out.println(ConsoleOutput.SYNC_SUCCESS.toString());
    }

    private void writeToFile(String path, String str) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path, false));

        writer.append(str);
        writer.close();
    }

    private void get(String name) {
        if (name == "") {
            System.out.println(ConsoleOutput.INVALID_FILE_NAME.toString());
            return;
        }

        Credentials credentials = getCurrentUser();
        if(credentials == null){
            System.out.println(ConsoleOutput.INVALID_CREDENTIALS.toString());
            return;
        }

        File file = new File(pathClientFiles + name);
        String fileContent = "";
        String checksum = null;

        if (file.exists()) {
            try {
                checksum = md5Checksum(pathClientFiles + name);
            } catch (NoSuchAlgorithmException e){
                System.err.println("Error: " + e.getMessage());
            }
        }

        try {
            fileContent = fileSystemStub.get(credentials, name, checksum);
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
        }

        if (fileContent == null && file.exists()) {
            System.out.println(ConsoleOutput.CONTENT_IS_ALREADY_UP_TO_DATE.toString());
        }
        else if(fileContent != null && !file.exists()){
            try {
                file.createNewFile();
                writeToFile(pathClientFiles + name, fileContent);
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        else if(fileContent != null && file.exists()){
            try{
                writeToFile(pathClientFiles + name, fileContent);
            } catch (IOException e){
                System.err.println(e.getMessage());
            }
            System.out.println(ConsoleOutput.CONTENT_UPDATED.toString());
        }

    }

    private void createFileLocally(String name){
        Path file = Paths.get(pathClientFiles + name);
        List<String> s = Arrays.asList("");

        try {
            Files.write(file, s , Charset.forName("UTF-8"));
        } catch (IOException e){
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void lock(String name) {
        if (name == "") {
            System.out.println(ConsoleOutput.INVALID_FILE_NAME.toString());
            return;
        }

        Credentials credentials = getCurrentUser();
        if(credentials == null){
            System.out.println(ConsoleOutput.INVALID_CREDENTIALS.toString());
            return;
        }

        //Is file stored server side
        HashSet<String> filesOnServer = getList();
        if(!filesOnServer.contains(name)){
            System.out.println(ConsoleOutput.FILE_404.toString());
            return;
        }

        //Is file stored client side
        File file = new File(pathClientFiles + name);
        String checksum = null;
        String content = getFileContent(name);
        Lock lock = null;

        if (!file.exists()) {
            // create file, add content
            createFileLocally(name);
            try {
                lock = (Lock) fileSystemStub.lock(credentials, null);
                writeToFile(pathClientFiles + name, lock.message);
            } catch (RemoteException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IOException e){
                System.err.println(e.getMessage());
            }

        } else {
            // get content and write in file
            try {
                checksum = md5Checksum(pathClientFiles + name);
            } catch (NoSuchAlgorithmException e) {
                System.err.println(e.getMessage());
            }

            try {
                lock = (Lock) fileSystemStub.lock(credentials, name);
                if(lock.isLocked){
                    try {
                        writeToFile(pathClientFiles + name, lock.message);
                        System.out.println(ConsoleOutput.LOCK_ACCEPTED.toString());
                    } catch (IOException e){
                        System.err.println(e.getMessage());
                    }
                } else {
                    System.out.println(lock.message);
                }
            } catch (RemoteException e){
                System.err.println(e.getMessage());
            }

        }
    }

    private void push(String name) {
        if (name == "") {
            System.out.println(ConsoleOutput.INVALID_FILE_NAME.toString());
            return;
        }

        Credentials credentials = getCurrentUser();
        if(credentials == null){
            System.out.println(ConsoleOutput.INVALID_CREDENTIALS.toString());
            return;
        }

        HashSet<String> filesOnServer = getList();
        if(!filesOnServer.contains(name)){
            System.out.println(ConsoleOutput.INVALID_FILE_NAME.toString());
            return;
        }

        boolean hasBeenPushed = false;
        String content = getFileContent(name);

        try {
            hasBeenPushed = fileSystemStub.push(credentials, new Document(name, null, content));
        } catch (RemoteException e) {
            System.out.println("Error: " + e.getMessage());
        }

        ConsoleOutput output = hasBeenPushed ? ConsoleOutput.PUSHED_ACCEPTED : ConsoleOutput.PUSHED_DENIED;
        System.out.println(output.toString());
    }

    private void writeToClientList(Credentials credentials) {
        String str = credentials.username + "@" + credentials.password;
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(pathClientList, true));
            writer.append(str + '\n');
            writer.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String getFileContent(String fileName) {
        try {
            return new String(Files.readAllBytes(Paths.get(pathClientFiles + fileName)));
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    private void setCurrentUser(Credentials credentials) {
        String str = credentials.username + "@" + credentials.password;
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(pathCurrentUser, false));
            writer.write(str);
            writer.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private Credentials getCurrentUser(){
        try {
            String line = new String(Files.readAllBytes(Paths.get(pathCurrentUser)));
            if(line == null || line == "")
                return null;
            else{
                String[] credentials = line.split("@");
                return new Credentials(credentials[0], credentials[1]);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    private String md5Checksum(String filePath) throws NoSuchAlgorithmException{

        //Create checksum for this file
        File file = new File(filePath);

        //Use MD5 algorithm
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");

        //Get the checksum
        String checksum = "";
        try {
            checksum = getFileChecksum(md5Digest, file);
        } catch (IOException e){
            System.err.println("Error: " + e.getMessage());
        }

        return checksum;
    }

    private static String getFileChecksum(MessageDigest digest, File file) throws IOException
    {
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };

        //close the stream; We don't need it now.
        fis.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }
}
