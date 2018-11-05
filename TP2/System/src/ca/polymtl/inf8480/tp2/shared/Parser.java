package ca.polymtl.inf8480.tp2.shared;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

public class Parser {

    public static <T> T parseJson(String filename, Class<T> tClass) throws IOException {
        Path configPath = Paths.get(filename);

        if (!Files.exists(configPath) || !configPath.toString().endsWith(".json")) {
            System.out.println("Could not find file...");
            throw new FileNotFoundException(filename);
        }

        System.out.println("Loading configuration for " + tClass.toString());
        String json = new String(Files.readAllBytes(configPath));

        T configs = null;
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            configs = gson.fromJson(json, tClass);
        } catch (JsonParseException jpe) {
            System.out.println("Unable to parse correctly ...");
            jpe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return configs;
    }

    public static ServerDetails loadLDAPDetails() throws IOException {
        return Parser.<ServerDetails>parseJson(Constants.LDAP_CONFIGS_PATH, ServerDetails.class);
    }
}
