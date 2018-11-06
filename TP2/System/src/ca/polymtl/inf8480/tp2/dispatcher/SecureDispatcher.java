package ca.polymtl.inf8480.tp2.dispatcher;

import ca.polymtl.inf8480.tp2.shared.IOperationServer;
import ca.polymtl.inf8480.tp2.shared.Operation;
import ca.polymtl.inf8480.tp2.shared.Task;
import ca.polymtl.inf8480.tp2.shared.TaskResult;

import ca.polymtl.inf8480.tp2.shared.exception.OverloadingServerException;

import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SecureDispatcher extends Dispatcher {

    public ConcurrentHashMap<String, Integer> overloadCount = new ConcurrentHashMap<>();

    @Override
    public void dispatch() {
        System.out.println("Dispatching Starting..");


        ExecutorService executor = Executors.newFixedThreadPool(this.operationServers.size());

        for (String calculationServerId : this.operationServerIds) {
            IOperationServer stub = this.operationServers.get(calculationServerId);
            if (stub != null) {

                int serverCapacity = 0;
                try {
                    serverCapacity = stub.getCapacity();

                } catch (RemoteException e) {
                    System.out.println("Unable to retrieve server capacity.");
                }

                final int capacityAndFactor = serverCapacity + this.configuration.capacityFactor;

                executor.execute(() -> {
                    while (this.pendingOperations.peek() != null) {

						int C = capacityAndFactor;
						if(this.overloadCount.containsKey(calculationServerId)){
							C = C - this.overloadCount.get(calculationServerId);
						}

                        List<Operation> toDo = new ArrayList<>();
                        for (int i = 0; i < C && this.pendingOperations.peek() != null; i++) {
                            Operation op = this.pendingOperations.poll();
                            toDo.add(op);
                        }
                        
                        if (!toDo.isEmpty()) {
                            try {
                                TaskResult tResult = stub.execute(this.configuration.credentials, new Task(toDo));
                                this.taskResults.add(tResult);
                                if (tResult.hadFailure instanceof OverloadingServerException) {
                                    this.incrementOverloadCount(calculationServerId);
                                    this.pendingOperations.addAll(toDo);                                    
                                }
                            } catch (RemoteException e) {
                                System.out.println(e.getMessage());
                                this.pendingOperations.addAll(toDo);
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

        this.setFinalResult();
    }

    private void incrementOverloadCount(String serverId) {
        if (!this.overloadCount.containsKey(serverId))
            this.overloadCount.put(serverId, 1);
        else{
			int oldValue = this.overloadCount.get(serverId);
			int newValue = oldValue + 1;
			this.overloadCount.put(serverId, newValue);
		}
    }
}
