package ca.polymtl.inf8480.tp2.shared;

import java.io.Serializable;

public class Credentials implements Serializable {

    public String username;
    public String password;

    public static Credentials of(String username, String password) {  // Credentials c = Crendentials.of(“name”,"psw");
        return new Credentials(username, password);
    }

    private Credentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

}
