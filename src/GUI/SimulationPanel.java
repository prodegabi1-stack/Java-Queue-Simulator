package GUI;

import Model.Server;
import Model.Task;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SimulationPanel extends JPanel {
    private List<Task> waitingTasks = new ArrayList<>();
    private List<Server> servers = new ArrayList<>();
    private boolean isRunning = false;

    public SimulationPanel() {
        this.setBackground(Color.WHITE);
        this.setPreferredSize(new Dimension(800, 600));
    }

    public void init(int queues) {
        this.isRunning = true;
        int newWidth = 300 + (queues * 200);
        this.setPreferredSize(new Dimension(newWidth, 1000));
        this.revalidate();
        this.repaint();
    }

    public void updateData(List<Task> waiting, List<Server> servers) {
        this.waitingTasks = new ArrayList<>(waiting);
        this.servers = new ArrayList<>(servers);

        int maxTasks = waitingTasks.size();
        for (Server s : servers) {
            maxTasks = Math.max(maxTasks, s.getTasks().length);
        }
        int newHeight = Math.max(600, 100 + (maxTasks * 50));
        int newWidth = 300 + (servers.size() * 200);

        this.setPreferredSize(new Dimension(newWidth, newHeight));
        this.revalidate();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!isRunning) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.setColor(new Color(44, 62, 80));
        g2.drawString("Waiting List (" + waitingTasks.size() + ")", 20, 30);

        for (int i = 0; i < waitingTasks.size(); i++) {
            g2.setColor(new Color(236, 240, 241));
            g2.fillRoundRect(20, 50 + (i * 45), 260, 40, 10, 10);
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.drawString("ID: " + waitingTasks.get(i).getId() + " Arrival time: " + waitingTasks.get(i).getArrivalTime() +  " Service time: " + waitingTasks.get(i).getServiceTime(), 30, 75 + (i * 45));
        }

        for (int i = 0; i < servers.size(); i++) {
            int x = 300 + (i * 200);
            int panelHeight = getHeight();

            if (i % 2 == 0) {
                g2.setColor(new Color(230, 230, 230));
            } else {
                g2.setColor(new Color(245, 245, 245));
            }

            g2.fillRect(x, 0, 200, panelHeight);

            g2.setColor(new Color(52, 73, 94));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.drawString("Queue " + (i + 1), x + 65, 30);

            Task[] tasks = servers.get(i).getTasks();
            for (int j = 0; j < tasks.length; j++) {
                Task t = tasks[j];

                g2.setColor(j == 0 ? new Color(46, 204, 113) : new Color(52, 152, 219));

                int yPos = 55 + (j * 45);
                g2.fillRoundRect(x + 20, yPos, 160, 40, 10, 10);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));

                if (j == 0) {
                    g2.drawString("ID: " + t.getId() + " Time left: " + t.getServiceTime(), x + 35, yPos + 25);
                } else {
                    g2.drawString("ID: " + t.getId() + " Service time: " + t.getServiceTime(), x + 35, yPos + 25);
                }
            }
        }
    }
}