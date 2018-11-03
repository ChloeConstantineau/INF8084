package ca.polymtl.inf8480.tp2.dispatcher;

import ca.polymtl.inf8480.tp2.shared.Operation;
import ca.polymtl.inf8480.tp2.shared.OperationType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Operation file loader
public class TaskLoader {

    ArrayList<Operation> myList = new ArrayList<>();
    static final String PELL = "pell";
    static final String PRIME = "prime";

    public ArrayList<Operation> getTask(String filePath) throws IOException{
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String str;
        while ((str = br.readLine()) != null) {
            addOperation(str);
        }

        return myList;
    }

    private void addOperation(String s) throws IOException {
        List<String> line = Arrays.asList(s.split(" "));

        if(line.size() != 2){
            throw new IOException();
        }

        int value = Integer.parseInt(line.get(1));

        switch (line.get(0)){
            case PELL:
                myList.add(Operation.of(OperationType.Pell, value));
                break;
            case PRIME:
                myList.add(Operation.of(OperationType.Prime, value));
                break;
            default:
                break;

        }
    }

}
