package GUI;

import BusinessLogic.SelectionPolicy;
import BusinessLogic.SimulationManager;
import Model.Server;
import Model.Task;
import javax.swing.*;
import java.util.List;

public class SimulationFrame extends JFrame {
    private JPanel panel1;
    private JButton startSimulationButton;
    private JTextField clientsTextField, minArrivalTimeTextField, queuesTextField, minServiceTimeTextField, maxArrivalTimeTextField, maxServiceTimeTextField, maxSimulationTimeTextField;
    private JComboBox<SelectionPolicy> strategyComboBox;
    private JLabel timeLabel;
    private JButton statistics;

    private JPanel animationPanel;
    private JButton test;

    private double lastAvgWait, lastAvgSer;
    private int lastPeak;

    private void createUIComponents() {
        animationPanel = new SimulationPanel();
    }

    public SimulationFrame() {
        this.setTitle("Queues Simulator - Animated");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(panel1);
        this.pack();
        this.setSize(1100, 800);

        strategyComboBox.setModel(new DefaultComboBoxModel<>(SelectionPolicy.values()));
        statistics.setEnabled(false);

        test.addActionListener(e -> {
            int testTimeLimit = 17;
            int testMinArrival = 2;
            int testMaxArrival = 12;
            int testMinService = 2;
            int testMaxService = 5;
            int testClients = 15;
            int testServers = 4;
            SelectionPolicy testPolicy = SelectionPolicy.SHORTEST_TIME;
            ((SimulationPanel) animationPanel).init(testServers);
            SimulationManager manager = new SimulationManager(testTimeLimit, testMinArrival, testMaxArrival, testMinService, testMaxService, testClients, testServers, testPolicy, this);
            Thread simulationThread = new Thread(manager);
            simulationThread.start();
            test.setEnabled(false);
            startSimulationButton.setEnabled(false);
        });

        startSimulationButton.addActionListener(e -> {
            try {
                startSimulationButton.setEnabled(false);
                statistics.setEnabled(false);
                int clients = Integer.parseInt(clientsTextField.getText());
                int queues = Integer.parseInt(queuesTextField.getText());
                int simTime = Integer.parseInt(maxSimulationTimeTextField.getText());
                int minArr = Integer.parseInt(minArrivalTimeTextField.getText());
                int maxArr = Integer.parseInt(maxArrivalTimeTextField.getText());
                int minSer = Integer.parseInt(minServiceTimeTextField.getText());
                int maxSer = Integer.parseInt(maxServiceTimeTextField.getText());
                SelectionPolicy policy = (SelectionPolicy) strategyComboBox.getSelectedItem();

                ((SimulationPanel) animationPanel).init(queues);
                SimulationManager manager = new SimulationManager(
                        simTime, minArr, maxArr, minSer, maxSer,
                        clients, queues, policy, this
                );
                new Thread(manager).start();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Eroare: " + ex.getMessage());
                startSimulationButton.setEnabled(true);
            }
        });

        statistics.addActionListener(e -> displayStats(lastAvgWait, lastAvgSer, lastPeak));
    }

    public void updateTime(int currentTime) {
        SwingUtilities.invokeLater(() -> timeLabel.setText("Time: " + currentTime));
    }

    public void updateAnimation(List<Task> waiting, List<Server> servers) {
        if (animationPanel instanceof SimulationPanel) {
            ((SimulationPanel) animationPanel).updateData(waiting, servers);
        }
    }

    public void showFinalStats(double avgWait, double avgSer, int peak) {
        this.lastAvgWait = avgWait;
        this.lastAvgSer = avgSer;
        this.lastPeak = peak;
        SwingUtilities.invokeLater(() -> {
            displayStats(avgWait, avgSer, peak);
            startSimulationButton.setEnabled(true);
            statistics.setEnabled(true);});
    }

    private void displayStats(double avgWait, double avgSer, int peak) {
        String message = String.format("Average Waiting Time: %.2f\nAverage Service Time: %.2f\nPeak Hour: %d", avgWait, avgSer, peak);
        JOptionPane.showMessageDialog(this, message, "Statistici", JOptionPane.INFORMATION_MESSAGE);
    }
}
