package ca.polymtl.inf8480.tp2.dispatcher;

import ca.polymtl.inf8480.tp2.shared.IOperationServer;
import ca.polymtl.inf8480.tp2.shared.Operation;
import ca.polymtl.inf8480.tp2.shared.Task;
import ca.polymtl.inf8480.tp2.shared.TaskResult;
import ca.polymtl.inf8480.tp2.shared.exception.OverloadingServerException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SecureDispatcher extends Dispatcher {

    @Override
    public void dispatch() {
        ExecutorService executor = Executors.newFixedThreadPool(this.operationServers.size());

        for (Map.Entry<Integer, IOperationServer> calculationServer : this.operationServers.entrySet()) {
            executor.execute(() -> {
                IOperationServer stub = calculationServer.getValue();
                int capacity = 1;
                try {
                    capacity = stub.getCapacity();
                }catch(RemoteException e){
                    System.out.println("Unable to retrieve server capacity");
                }
                System.out.println("Server capacity: 4");
                while (this.pendingOperations.peek() != null) {
                    List<Operation> toDo = new ArrayList<>();

                    for (int i = 0; i < capacity && this.pendingOperations.peek() != null; i++) {
                        Operation op = this.pendingOperations.poll();
                        toDo.add(op);
                    }

                    if (!toDo.isEmpty()) {

                        Task task = new Task(toDo);

                        try {
                            TaskResult tResult = stub.execute(this.configuration.credentials, task);

                            //TODO check if tResult hasFailure and deal accordingly
                            this.taskResults.add(tResult);
                        } catch (OverloadingServerException e) {
                            System.out.println(e.getMessage());
                        }catch(RemoteException e){
                            System.out.println(e.getMessage());
                        }
                    }
                }
            });
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
        }
    }
}
