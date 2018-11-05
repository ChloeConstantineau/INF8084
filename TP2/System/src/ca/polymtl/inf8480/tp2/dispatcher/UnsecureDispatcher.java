package ca.polymtl.inf8480.tp2.dispatcher;

import ca.polymtl.inf8480.tp2.shared.IOperationServer;
import ca.polymtl.inf8480.tp2.shared.Operation;
import ca.polymtl.inf8480.tp2.shared.Task;
import ca.polymtl.inf8480.tp2.shared.TaskResult;
import ca.polymtl.inf8480.tp2.shared.exception.OverloadingServerException;

import java.rmi.RemoteException;
import java.util.ArrayList;
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

        while (this.operationServers.size() > 1 && this.pendingOperations.peek() != null) {

            ExecutorService executor = Executors.newFixedThreadPool(this.operationServers.size());

            for (String calculationServerId : this.operationServerIds) {
                IOperationServer stub = this.operationServers.get(calculationServerId);
                if (stub != null) {
                    executor.execute(() -> {
                        int capacity = this.averageCapacity;
                        try {
                            capacity = stub.getCapacity();
                            capacity += this.configuration.capacityFactor;
                        } catch (RemoteException e) {
                            System.out.println("Unable to retrieve server specific capacity.");
                        }

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

                                    if (tResult.hadFailure == null) {
                                        this.taskResults.add(tResult);
                                    } else if (tResult.hadFailure instanceof OverloadingServerException) {
                                        this.makeTaskEasier();
                                        this.populatePendingOperations(toDo);
                                    }
                                } catch (RemoteException e) {
                                    System.out.println(e.getMessage());
                                    this.populatePendingOperations(toDo);
                                    break;
                                }
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
