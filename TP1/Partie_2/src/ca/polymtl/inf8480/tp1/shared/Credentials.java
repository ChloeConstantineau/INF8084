package ca.polymtl.inf8480.tp1.shared;

public class Credentials implements java.io.Serializable  {
    public String username;
    public String password;

    public Credentials(String username, String password){
       this.username = username;
       this.password = password;
    }
}
