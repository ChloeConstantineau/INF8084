package ca.polymtl.inf8480.tp1.shared;

import java.util.Objects;

public class Credentials implements java.io.Serializable  {
    public String username;
    public String password;

    public Credentials(String username, String password){
       this.username = username;
       this.password = password;
    }

    @Override
    public boolean equals(Object obj){

        if(obj == this){
            return true;
        }
        if(!(obj instanceof Credentials)){
            return false;
        }

        Credentials credentials = (Credentials) obj;
        return username == ((Credentials) obj).username &&
                password == ((Credentials) obj).password;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
