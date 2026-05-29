package BusinessLogic;

import GUI.SimulationFrame;
import Model.Server;
import Model.Task;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable {
    private int timeLimit;
    private int maxProcessingTime;
    private int minProcessingTime;
    private int minArrivalTime;
    private int maxArrivalTime;
    private int numberOfServers;
    private int numberOfClients;
    private SelectionPolicy selectionPolicy;
    private Scheduler scheduler;
    private SimulationFrame frame;
    private List<Task> tasks;

    private final Object lock = new Object();
    private final AtomicInteger currentTick = new AtomicInteger(0);
    private final AtomicInteger serversFinished = new AtomicInteger(0);

    public SimulationManager(int timeLimit, int minArrival, int maxArrival, int minService, int maxService, int clients, int servers, SelectionPolicy policy, SimulationFrame frame) {
        this.timeLimit = timeLimit;
        this.minArrivalTime = minArrival;
        this.maxArrivalTime = maxArrival;
        this.minProcessingTime = minService;
        this.maxProcessingTime = maxService;
        this.numberOfClients = clients;
        this.numberOfServers = servers;
        this.selectionPolicy = policy;
        this.frame = frame;

        this.scheduler = new Scheduler(numberOfServers, lock, currentTick, serversFinished);
        this.scheduler.changeStrategy(selectionPolicy);
        generateNRandomTasks();
    }

    private void generateNRandomTasks() {
        Random random = new Random();
        this.tasks = new ArrayList<>();
        for (int index = 0; index < numberOfClients; index++) {
            int arrival = random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            int service = random.nextInt(maxProcessingTime - minProcessingTime + 1) + minProcessingTime;
            tasks.add(new Task(arrival, service));
        }
        Collections.sort(tasks, Comparator.comparingInt(Task::getArrivalTime));
    }

    public void run() {
        int currentTime = 0;
        double totalWaitingTime = 0;
        int waitingClients = 0;
        double totalServiceTime = 0;
        int servedClients = 0;
        int peakHour = 0;
        int maxClientsAtOnce = -1;

        try (PrintWriter writer = new PrintWriter(new FileWriter("log.txt"))) {
            while (currentTime <= timeLimit) {

                synchronized (lock) {
                    serversFinished.set(0);
                    currentTick.incrementAndGet();
                    lock.notifyAll();

                    while (serversFinished.get() < numberOfServers) {
                        lock.wait();
                    }
                }

                Iterator<Task> taskIterator = tasks.iterator();
                while (taskIterator.hasNext()) {
                    Task task = taskIterator.next();
                    if (task.getArrivalTime() == currentTime) {
                        Server server = scheduler.dispatchTask(task);
                        int waitingTime = server.getWaitingPeriod().get() - task.getServiceTime();
                        if(currentTime + waitingTime <= timeLimit){
                            totalWaitingTime += waitingTime;
                            waitingClients++;
                        }
                        if(currentTime + waitingTime + task.getServiceTime() <= timeLimit){
                            totalServiceTime += task.getServiceTime();
                            servedClients++;
                        }
                        taskIterator.remove();
                    }
                }

                int currentTotalClients = 0;
                for (Server server : scheduler.getServers()) {
                    currentTotalClients += server.getTasks().length;
                }
                if (currentTotalClients > maxClientsAtOnce) {
                    maxClientsAtOnce = currentTotalClients;
                    peakHour = currentTime;
                }

                String[] queueStatuses = new String[numberOfServers];
                for (int index = 0; index < numberOfServers; index++) {
                    queueStatuses[index] = scheduler.getServers().get(index).toString();
                }

                frame.updateAnimation(tasks, scheduler.getServers());
                frame.updateTime(currentTime);

                writer.println("Time " + currentTime);
                writer.println("Waiting clients: " + (tasks.isEmpty() ? "none" : tasks.toString()));
                for (int index = 0; index < numberOfServers; index++) {
                    writer.println("Queue " + (index + 1) + ": " + (queueStatuses[index].isEmpty() ? "closed" : queueStatuses[index]));
                }
                writer.println();

                currentTime++;
                Thread.sleep(1000);
            }

            double averageWaiting = 0;
            double averageService = 0;
            if(waitingClients!=0) {
                averageWaiting = totalWaitingTime / waitingClients;
            }
            if(servedClients!=0) {
                averageService = totalServiceTime / servedClients;
            }

            writer.println("Average waiting time: " + String.format("%.2f", averageWaiting));
            writer.println("Average service time: " + String.format("%.2f", averageService));
            writer.println("Peak hour: " + peakHour);

            frame.showFinalStats(averageWaiting, averageService, peakHour);
            scheduler.stopServers();

        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}