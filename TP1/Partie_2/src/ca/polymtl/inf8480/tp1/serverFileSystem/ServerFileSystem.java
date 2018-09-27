package ca.polymtl.inf8480.tp1.serverFileSystem;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ca.polymtl.inf8480.tp1.shared.Credentials;
import ca.polymtl.inf8480.tp1.shared.Document;
import ca.polymtl.inf8480.tp1.shared.FileSystemInterface;

public class ServerFileSystem implements FileSystemInterface {

    HashMap<String, String> documentsMap = new HashMap<>();
    final String DIRECTORY_NAME = "./ServerSide/Files/";


    public static void main(String[] args) {
        ServerFileSystem server = new ServerFileSystem();
        server.run();
    }

    public ServerFileSystem() {
        super();
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
    }

    private void writeToFile(Credentials credentials) throws IOException {
//        String path = "./ServerSide/ClientList.txt";
//        String str = credentials.username + "@" + credentials.password;
//        BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
//
//        writer.append(str + '\n');
//        writer.close();
    }

    private void readFromFile() throws  IOException {
//        String path = "./ServerSide/ClientList.txt";
//        File file = new File(path);
//        BufferedReader br = new BufferedReader(new FileReader(file));
//
//        String str;
//        while ((str = br.readLine()) != null){
//            // TODO : Something
//        }
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


    /*
     * Méthode accessible par RMI.
     */
    public boolean create(String name) throws RemoteException {
        if(documentsMap.containsKey(name)){
            return false;
        }

        Path file = Paths.get(DIRECTORY_NAME + name + ".txt");
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

        return true;
    }

    public String list() throws RemoteException {
        return null;
    }

    public String get(String name, String checksum) throws RemoteException {
        return null;
    }
}
