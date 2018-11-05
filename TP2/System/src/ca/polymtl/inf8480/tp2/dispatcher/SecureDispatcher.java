package ca.polymtl.inf8480.tp2.dispatcher;

import ca.polymtl.inf8480.tp2.shared.IOperationServer;
import ca.polymtl.inf8480.tp2.shared.Operation;
import ca.polymtl.inf8480.tp2.shared.Task;
import ca.polymtl.inf8480.tp2.shared.TaskResult;
import ca.polymtl.inf8480.tp2.shared.exception.OverloadingServerException;

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
                int capacity = stub.getCapacity();

                while (this.pendingOperations.peek() != null) {
                    List<Operation> toDo = new ArrayList<>();

                    for (int i = 0; i < capacity && this.pendingOperations.peek() != null; i++) {
                        toDo.add(this.pendingOperations.poll());
                    }

                    if (!toDo.isEmpty()) {

                        Task task = new Task(toDo);

                        try {
                            TaskResult tResult = stub.execute(this.configuration.credentials, task);

                            //TODO check if tResult hasFailure and deal accordingly
                            this.taskResults.add(tResult);
                        } catch (OverloadingServerException e) {
                            System.out.println("Server " + stub.getId() + "");
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
