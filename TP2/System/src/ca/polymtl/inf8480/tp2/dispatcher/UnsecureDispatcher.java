package ca.polymtl.inf8480.tp2.dispatcher;

import ca.polymtl.inf8480.tp2.shared.*;
import ca.polymtl.inf8480.tp2.shared.exception.OverloadingServerException;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UnsecureDispatcher extends Dispatcher {

    @Override
    public void dispatch() {
        if (this.operationServers.size() <= 1) {
            System.out.println("Not enough servers present to validate results...Shutting down.");
            return;
        }

        List<String> lostServers = new ArrayList<>();

        while (this.operationServers.size() > 1 && this.pendingOperations.peek() != null) {

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

                                if (tResult.hadFailure == null) {
                                    roundResult.add(tResult.result);
                                } else if (tResult.hadFailure instanceof OverloadingServerException) {
                                    this.makeTaskEasier();
                                    this.populatePendingOperations(toDo);
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

            //Remove dead servers if any
            if(lostServers.size() > 0){
                for (String lostSoul: lostServers) {
                    this.operationServerIds.remove(lostSoul);
                }
                lostServers.clear();
            }

            //Is result valid
            if(roundResult.size() > 2) {
                //Find duplicate
                ConcurrentLinkedQueue<Integer> copyResult = roundResult;
                boolean foundDuplicate = false;

                for (int result : copyResult) {
                    roundResult.remove(result);
                    if (roundResult.contains(result)) {
                        foundDuplicate = true;
                        this.taskResults.add(TaskResult.of(result, null)); //At lest two servers found this result, must be good
                    }
                }

                //Will redo operations if results not valid
                if (!foundDuplicate)
                    this.populatePendingOperations(toDo);
            }else{
                this.populatePendingOperations(toDo);
            }
        }

        this.setFinalResult();
    }
}
