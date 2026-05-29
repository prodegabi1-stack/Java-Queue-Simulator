package BusinessLogic;

import Model.Server;
import Model.Task;
import java.util.List;

public class ShortestQueueStrategy implements Strategy {
    public Server addTask(List<Server> servers, Task task) {
        Server bestServer = servers.get(0);
        int minLength = bestServer.getTasks().length;
        int length;
        for (Server server : servers) {
            length = server.getTasks().length;
            if (length < minLength) {
                minLength = length;
                bestServer = server;
            }
        }
        bestServer.addTask(task);
        return bestServer;
    }
}
