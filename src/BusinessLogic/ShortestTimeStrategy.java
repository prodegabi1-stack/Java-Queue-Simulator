package BusinessLogic;

import Model.Server;
import Model.Task;
import java.util.List;

public class ShortestTimeStrategy implements Strategy {
    public Server addTask(List<Server> servers, Task task) {
        Server bestServer = servers.get(0);
        int minTime = bestServer.getWaitingPeriod().get();
        int time;
        for (Server server : servers) {
            time = server.getWaitingPeriod().get();
            if (time < minTime) {
                minTime = time;
                bestServer = server;
            }
        }
        bestServer.addTask(task);
        return bestServer;
    }
}
