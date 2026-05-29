package BusinessLogic;

import Model.Server;
import Model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Scheduler {
    private List<Server> servers;
    private int maxNoServers;
    private Strategy strategy;
    private ExecutorService pool;

    public Scheduler(int maxNoServers, Object lock, AtomicInteger currentTick, AtomicInteger serversFinished) {
        this.maxNoServers = maxNoServers;
        this.servers = new ArrayList<>();
        this.pool = Executors.newFixedThreadPool(maxNoServers);

        for (int index = 0; index < this.maxNoServers; index++) {
            Server server = new Server(lock, currentTick, serversFinished);
            servers.add(server);
            pool.execute(server);
        }
    }

    public void changeStrategy(SelectionPolicy policy) {
        if (policy == SelectionPolicy.SHORTEST_QUEUE) {
            strategy = new ShortestQueueStrategy();
        }
        if (policy == SelectionPolicy.SHORTEST_TIME) {
            strategy = new ShortestTimeStrategy();
        }
    }

    public Server dispatchTask(Task task) {
        return strategy.addTask(servers, task);
    }

    public List<Server> getServers() {
        return servers;
    }

    public void stopServers() {
        pool.shutdownNow();
    }
}