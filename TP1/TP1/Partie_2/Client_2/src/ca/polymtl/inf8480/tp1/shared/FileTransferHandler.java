package ca.polymtl.inf8480.tp1.shared;

public class FileTransferHandler implements java.io.Serializable  {

    Document file;
    String message;

    FileTransferHandler(Document file, String message){
        this.file = file;
        this.message = message;
    }
}
