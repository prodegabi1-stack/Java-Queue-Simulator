# Multithreaded Queue Management Engine 🚀⏳

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Swing](https://img.shields.io/badge/GUI-Swing-blue?style=for-the-badge)
![Multithreading](https://img.shields.io/badge/Concurrency-Multithreading-brightgreen?style=for-the-badge)

## 📌 Overview
[cite_start]This project is a high-concurrency **Discrete Event Simulation (DES)** engine built in Java[cite: 27, 29]. [cite_start]It models complex multi-server queuing systems where clients (tasks) arrive at random intervals and are processed concurrently by a pool of parallel servers[cite: 27, 29]. 

[cite_start]The core objective is to simulate, visualize, and analyze different load-balancing strategies in a multithreaded environment, rigorously testing thread synchronization, resource pooling, and real-time data consistency[cite: 27, 29, 30].

---

## 🚀 Key Features
* **Deterministic Concurrency:** Utilizes a custom tick-based synchronization mechanism using Java Monitors (`wait`/`notifyAll`) to ensure all parallel worker threads stay temporally aligned.
* [cite_start]**Dynamic Load Balancing:** Implements the **Strategy Design Pattern** to allow real-time switching between dispatching algorithms:
  * [cite_start]`Shortest Queue`: Routes new clients to the server with the fewest waiting individuals.
  * [cite_start]`Shortest Time`: Routes new clients to the server with the lowest cumulative waiting time.
* [cite_start]**Thread-Safe Architecture:** Employs `AtomicInteger`, synchronized data structures, and thread pools (`ExecutorService`) to guarantee data integrity without heavy lock contention[cite: 30].
* [cite_start]**Real-Time Observability:** Features a dynamic Swing-based GUI that visually animates the queues and tracks performance KPIs in real-time[cite: 27, 29].
* **Data Analytics & Logging:** Automatically calculates post-simulation metrics such as **Average Waiting Time**, **Average Service Time**, and **Peak Hour**, while outputting a detailed state matrix to `log.txt`.

---

## 🏗️ Architecture & Flow
1. **Producer (`SimulationManager`)**: Generates randomized tasks and advances the simulation clock tick-by-tick.
2. **Dispatcher (`Scheduler`)**: Receives incoming tasks and applies the selected `SelectionPolicy` to route them to the optimal server.
3. [cite_start]**Consumers (`Server`)**: Independent threads running in a pool that consume tasks from their local queue, process them over time, and signal completion[cite: 30].

---

## 🛠️ Technologies Used
* [cite_start]**Language:** Java 17+ 
* [cite_start]**Concurrency:** `java.util.concurrent` (Thread Pools, Concurrency API, Atomic Variables) [cite: 27, 30]
* [cite_start]**Design Patterns:** Strategy Pattern, Producer-Consumer 
* [cite_start]**UI:** Java Swing 

---

## ⚙️ How to Run

1. Clone the repository:
   ```bash
   git clone [https://github.com/yourusername/Queue-Management-System.git](https://github.com/yourusername/Queue-Management-System.git)
