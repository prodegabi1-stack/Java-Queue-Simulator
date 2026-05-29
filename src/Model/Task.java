package Model;

public class Task {
    private int id;
    private int arrivalTime;
    private int serviceTime;
    private static int nextID = 1;

    public Task(int arrivalTime, int serviceTime) {
        this.id = nextID++;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public int getId() { return id; }
    public int getArrivalTime() { return arrivalTime; }
    public int getServiceTime() { return serviceTime; }
    public void setServiceTime(int serviceTime) { this.serviceTime = serviceTime; }

    @Override
    public String toString() {
        return "Id: " + id + ", AT: " + arrivalTime + ", ST: " + serviceTime;
    }
}