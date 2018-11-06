package ca.polymtl.inf8480.tp2.dispatcher;

import ca.polymtl.inf8480.tp2.shared.*;
import ca.polymtl.inf8480.tp2.shared.exception.OverloadingServerException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UnsecureDispatcher extends Dispatcher {

    private ConcurrentHashMap<String, Integer> overloadCount = new ConcurrentHashMap<>();

    @Override
    public void dispatch() {
        if (this.operationServers.size() <= 1) {
            System.out.println("Not enough servers present to validate results...Shutting down.");
            return;
        }

        List<String> lostServers = new ArrayList<>();
        int nbRetry = 0;
        boolean hasAbandonedSome = false;

        while (this.operationServerIds.size() >= 2 && this.pendingOperations.peek() != null) {
            System.out.println(this.pendingOperations.size());
            List<Operation> toDo = new ArrayList<>();
            for (int i = 0; i < averageCapacity + this.configuration.capacityFactor && this.pendingOperations.peek() != null; i++) {
                Operation op = this.pendingOperations.poll();
                toDo.add(op);
            }

            ConcurrentLinkedQueue<Integer> roundResult = new ConcurrentLinkedQueue<>();


            ExecutorService executor = Executors.newFixedThreadPool(this.operationServers.size());

            for (String calculationServerId : this.operationServerIds) {
                IOperationServer stub = this.operationServers.get(calculationServerId);
                if (stub != null) {
                    executor.execute(() -> {
                        if (!toDo.isEmpty()) {

                            Task task = new Task(toDo);

                            try {
                                TaskResult tResult = stub.execute(this.configuration.credentials, task);
                                roundResult.add(tResult.result);

                                if (tResult.hadFailure instanceof OverloadingServerException) {
                                    this.incrementOverloadCount(calculationServerId);
                                }
                            } catch (RemoteException e) {
                                System.out.println(e.getMessage());
                                lostServers.add(calculationServerId);
                            }
                        }
                    });
                }
            }

            executor.shutdown();
            while (!executor.isTerminated()) {
            }

            System.out.println("Finishes thread?");

            //Remove dead servers if any
            if (lostServers.size() > 0) {
                for (String lostSoul : lostServers) {
                    this.operationServerIds.remove(lostSoul);
                }
                lostServers.clear();
            }

            System.out.println(roundResult);

            //Is result valid
            if (roundResult.size() >= 2) {
                //Find duplicate
                ConcurrentLinkedQueue<Integer> copyResult = roundResult;
                boolean foundDuplicate = false;

                for (int result : copyResult) {
                    roundResult.remove(result);
                    if (roundResult.contains(result)) {
                        foundDuplicate = true;
                        this.taskResults.add(TaskResult.of(result, null)); //At least two servers found this result, must be good
                    }
                }

                System.out.println(foundDuplicate);
                System.out.println(nbRetry < Constants.NB_MAX_RETRY);

                //Will redo operations if results not valid
                if (!foundDuplicate) {
					nbRetry++;
					if(nbRetry < Constants.NB_MAX_RETRY){
						this.pendingOperations.addAll(toDo);
					}                    
                }
            } else {
                this.pendingOperations.addAll(toDo);
            }

            if (nbRetry == Constants.NB_MAX_RETRY) {
                System.out.println("Setting nbRety to 0");
                nbRetry = 0;
                hasAbandonedSome = true;
                continue;
            }
        }

        if (hasAbandonedSome) {
            System.out.println("Had to abandon some operations, result is not exact..");
        }

        this.setFinalResult();
    }

    private void incrementOverloadCount(String serverId) {
        if (!this.overloadCount.containsKey(serverId))
            this.overloadCount.put(serverId, 1);
        else {
            int oldValue = this.overloadCount.get(serverId);
            int newValue = oldValue + 1;
            this.overloadCount.put(serverId, newValue);
        }
    }
}
