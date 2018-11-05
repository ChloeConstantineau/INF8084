package ca.polymtl.inf8480.tp2.dispatcher;

import ca.polymtl.inf8480.tp2.shared.IOperationServer;
import ca.polymtl.inf8480.tp2.shared.Operation;
import ca.polymtl.inf8480.tp2.shared.Task;
import ca.polymtl.inf8480.tp2.shared.TaskResult;
import ca.polymtl.inf8480.tp2.shared.exception.OverloadingServerException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UnsecureDispatcher extends Dispatcher {

    @Override
    public void dispatch() {
        if (this.operationServers.size() <= 1) {
            System.out.println("Not enough servers present to validate results...Shutting down.");
            return;
        }

        HashMap<String, TaskResult> taskResultsPerServer = new HashMap<>();

        while (this.operationServers.size() > 1 && this.pendingOperations.peek() != null) {

            List<Operation> toDo = new ArrayList<>();
            for (int i = 0; i < averageCapacity + this.configuration.capacityFactor && this.pendingOperations.peek() != null; i++) {
                Operation op = this.pendingOperations.poll();
                toDo.add(op);
            }

            ExecutorService executor = Executors.newFixedThreadPool(this.operationServers.size());

            for (String calculationServerId : this.operationServerIds) {
                IOperationServer stub = this.operationServers.get(calculationServerId);
                if (stub != null) {
                    executor.execute(() -> {
                        if (!toDo.isEmpty()) {

                            Task task = new Task(toDo);

                            try {
                                TaskResult tResult = stub.execute(this.configuration.credentials, task);

                                if (tResult.hadFailure == null) {
                                    this.taskResults.add(tResult);
                                } else if (tResult.hadFailure instanceof OverloadingServerException) {
                                    this.makeTaskEasier();
                                    this.populatePendingOperations(toDo);
                                }
                            } catch (RemoteException e) {
                                System.out.println(e.getMessage());
                                this.populatePendingOperations(toDo);
                            }
                        }
                    });
                }
            }

            executor.shutdown();
            while (!executor.isTerminated()) {
            }
        }
        this.setFinalResult();
    }
}
