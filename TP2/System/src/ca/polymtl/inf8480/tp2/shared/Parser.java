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

    public static <T> T parseJson(String filename, Class<T> clazz) throws IOException {
        Path configPath = Paths.get(filename);

        if (!Files.exists(configPath) || !configPath.toString().endsWith(".json")) {
            throw new FileNotFoundException(filename);
        }

        System.out.println("Loading configuration for " +  clazz.toString());
        String json = new String(Files.readAllBytes(configPath));

        T configs = null;
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            configs = gson.fromJson(json, clazz);
        } catch (JsonParseException jpe) {
            System.out.println("Unable to parse correctly ...");
            jpe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return configs;
    }
}
