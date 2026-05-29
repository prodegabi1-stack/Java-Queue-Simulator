package Model;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private final Object lock;
    private final AtomicInteger currentTick;
    private final AtomicInteger serversFinished;
    private int localTick;

    public Server(Object lock, AtomicInteger currentTick, AtomicInteger serversFinished) {
        this.tasks = new LinkedBlockingQueue<>();
        this.waitingPeriod = new AtomicInteger(0);
        this.lock = lock;
        this.currentTick = currentTick;
        this.serversFinished = serversFinished;
        this.localTick = 0;
    }

    public void addTask(Task newTask) {
        tasks.add(newTask);
        this.waitingPeriod.addAndGet(newTask.getServiceTime());
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                synchronized (lock) {
                    while (currentTick.get() == localTick) {
                        lock.wait();
                    }
                }

                Task currentTask = tasks.peek();
                if (currentTask != null) {
                    currentTask.setServiceTime(currentTask.getServiceTime() - 1);
                    this.waitingPeriod.decrementAndGet();
                    if (currentTask.getServiceTime() <= 0) {
                        tasks.take();
                    }
                }

                synchronized (lock) {
                    localTick = currentTick.get();
                    serversFinished.incrementAndGet();
                    lock.notifyAll();
                }

            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public Task[] getTasks() {
        return tasks.toArray(new Task[0]);
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    @Override
    public String toString() {
        if (tasks.isEmpty()) return "Empty";
        StringBuilder stringBuilder = new StringBuilder();
        for (Task task : tasks) {
            stringBuilder.append(task.toString()).append("; ");
        }
        return stringBuilder.toString();
    }
}