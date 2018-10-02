package ca.polymtl.inf8480.tp1.serverFileSystem;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import ca.polymtl.inf8480.tp1.shared.*;

public class ServerFileSystem implements FileSystemInterface {

    // <document name, document checksum>
    private HashMap<String, String> documentsMap = new HashMap<>();
    // <document name, lock owner name>
    private HashMap<String, String> lockedDocuments = new HashMap<>();
    private AuthenticationInterface authServerStub;

    private final String DIRECTORY_NAME = "./ServerSide/Files/";
    private final String LOCKED_FILES = "./ServerSide/LockedFiles.txt";
    private final String SERVERHOST_NAME = "127.0.0.1";


    public static void main(String[] args) {
        ServerFileSystem server = new ServerFileSystem();
        server.run();
    }

    public ServerFileSystem() {
        super();

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        authServerStub = (AuthenticationInterface) loadServerStub(SERVERHOST_NAME, "serverAuth");

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

    private void run() {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            FileSystemInterface stub = (FileSystemInterface) UnicastRemoteObject
                    .exportObject(this, 0);

            Registry registry = LocateRegistry.getRegistry();
            registry.rebind("serverFileSystem", stub);
            System.out.println("serverFileSystem ready.");
        } catch (ConnectException e) {
            System.err
                    .println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
            System.err.println();
            System.err.println("Erreur: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
        }

        // when starting server, read from file to see what file I have
        // add to hashset
        listFiles();

        // Add locked document to hashmap
        try{
            getLockedFiles();
        } catch (IOException e){
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void writeToFile(String path, String str) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path, false));
        writer.append(str);
        writer.close();
    }

    private void writeLockDocumentToFile(String name, String owner) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(LOCKED_FILES, true));
        writer.append(name + "@" + owner + '\n');
        writer.close();
    }

    private String readFile(String name) throws  IOException {
        String path = DIRECTORY_NAME + name;
        File file = new File(path);
        BufferedReader br = new BufferedReader(new FileReader(file));

        StringBuilder sc = new StringBuilder();

        String str;
        while ((str = br.readLine()) != null){
            sc.append(str + '\n');
        }

        // remove last '\n'
        String s = sc.toString();
        return s.substring(0, s.length() - 1);
    }

    private void listFiles() {
        File directory = new File(DIRECTORY_NAME);

        //get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList){
            if (file.isFile()){
                String name = file.getName();

                try{
                    documentsMap.put(name, md5Checksum(DIRECTORY_NAME + name));
                } catch (NoSuchAlgorithmException e){
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }

    private void getLockedFiles() throws IOException {
        File file = new File(LOCKED_FILES);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String str;
        while ((str = br.readLine()) != null){
            addDocumentToMap(str);
        }

    }

    private void addDocumentToMap(String s){
        String documentName = s.substring(0, s.indexOf("@"));
        String lockOwner = s.substring(s.indexOf("@") + 1, s.length());
        lockedDocuments.put(documentName, lockOwner);
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

    private void removeFromLockedFile(String name) throws IOException{
        File file = new File(LOCKED_FILES);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String str;
        StringBuilder sc = new StringBuilder();

        while ((str = br.readLine()) != null){
            if(!name.equals(str.substring(0, str.indexOf("@")))){
                sc.append(str);
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(LOCKED_FILES, false));
        writer.append(sc.toString());
        writer.close();
    }


    /*
     * Méthode accessible par RMI.
     */
    public boolean create(Credentials credentials, String name) throws RemoteException {

        if(!authServerStub.verifyClient(credentials) || documentsMap.containsKey(name)){
            return false;
        }

        Path file = Paths.get(DIRECTORY_NAME + name);
        List<String> s = Arrays.asList("");

        try {
            Files.write(file, s , Charset.forName("UTF-8"));
        } catch (IOException e){
            System.err.println("Error: " + e.getMessage());
        }

        try{
            documentsMap.put(name, md5Checksum(DIRECTORY_NAME + name));
        } catch (NoSuchAlgorithmException e){
            System.err.println("Error: " + e.getMessage());
        }

        System.out.println("Document created");
        return true;
    }

    public String list(Credentials credentials) throws RemoteException {
        if(!authServerStub.verifyClient(credentials)){
            return null;
        }

        StringBuilder sc = new StringBuilder();
        for(String name : documentsMap.keySet()){
            if(name.charAt(0) != '.'){
                sc.append(name);
                if(lockedDocuments.containsKey(name)){
                    sc.append(", locked by user " + lockedDocuments.get(name));
                }
                sc.append('\n');
            }
        }

        return sc.toString();
    }

    public String get(Credentials credentials, String name, String checksum) throws RemoteException {
        // credentials not good or document doesn't exists
        System.out.println("Getting file " + name);
        if(!authServerStub.verifyClient(credentials) || !documentsMap.containsKey(name)){
            System.out.println("Something went wrong");
            return null;
        }

        // if checksum of file on server is the same as client checksum
        if(documentsMap.get(name).equals(checksum)){
            System.out.println("Same checksum");
            return null;
        }

        System.out.println("Getting content");
        String content = null;
        try {
            content = readFile(name);
            System.out.println(content);
        } catch (IOException e){
            System.err.println("Error: " + e.getMessage());
        }

        return content;
    }

     public Lock lock(Credentials credentials, String name) throws RemoteException {
         if(!authServerStub.verifyClient(credentials) || !documentsMap.containsKey(name)){
             return new Lock(false, ConsoleOutput.FILE_404.toString());
         }

         if(lockedDocuments.containsKey(name)){
             String message = ConsoleOutput.FILE_ALREADY_LOCKED.toString() + lockedDocuments.get(name);
             return new Lock(false, message);
         }

         // send most recent version of the file to the client
         String content = null;
         try {
             content = readFile(name);
         } catch (IOException e){
             System.err.println("Error: " + e.getMessage());
         }

         // write lock info to file
         try {
             writeLockDocumentToFile(name, credentials.username);
         } catch (IOException e){
             System.err.println("Error: " + e.getMessage());
         }

         lockedDocuments.put(name, credentials.username);
         return new Lock(true, content);
     }

     public boolean push(Credentials credentials, Document file) throws RemoteException {
         if(!authServerStub.verifyClient(credentials)){
             return false;
         }

         if(!lockedDocuments.containsKey(file.name) || !lockedDocuments.get(file.name).equals(credentials.username)){
             return false;
         }

         try {
             writeToFile(DIRECTORY_NAME + file.name, file.content);
             documentsMap.put(file.name, file.checksum);
             lockedDocuments.remove(file.name);
             removeFromLockedFile(file.name);
         } catch (IOException e){
             System.err.println(e.getMessage());
         }

        return true;
     }

    public ArrayList<Document> syncLocalDirectory(Credentials credentials) throws RemoteException {
        if(!authServerStub.verifyClient(credentials)){
            return null;
        }

        ArrayList<Document> doccumentArray = new ArrayList<>();
        for(String name : documentsMap.keySet()){
            if(name.charAt(0) != '.'){
                try {
                    Document doc = new Document(name, documentsMap.get(name), readFile(name));
                    doccumentArray.add(doc);
                } catch (IOException e){
                    System.err.println(e.getMessage());
                }
            }
        }

        return doccumentArray;
    }
}
