import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.Map;

public class CarWashGUI extends ServiceStation {

  // GUI Components
  private JFrame frame;
  private JPanel pumpPanel;
  private JPanel waitingPanel;
  private JPanel arrivingPanel;
  private JPanel finishedPanel;
  private JTextArea logArea;
  private JTextField carIdField;
  private JButton addCarButton;
  private JTextField batchCountField;
  private JButton addBatchButton;
  private JLabel statusLabel;
  private Map<String, JLabel> pumpLabels;
  private Map<String, JLabel> waitingSlots;
  private JTextArea arrivingCarsArea;
  private JTextArea finishedCarsArea;
  private int carCounter = 0;
  private static Queue<String> finishedCars = new LinkedList<>();

  public CarWashGUI(int pumps, int waitingArea) {
    pumpCount = pumps;
    waitingAreaCount = waitingArea;
    pumpLabels = new HashMap<>();
    waitingSlots = new HashMap<>();

    frame = new JFrame("Car Wash Simulation");
    frame.setSize(1000, 700);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout(10, 10));

    // Top Panel - Controls
    JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    controlPanel.setBorder(BorderFactory.createTitledBorder("Add New Car"));

    JLabel carLabel = new JLabel("Car ID:");
    carIdField = new JTextField(10);
    addCarButton = new JButton("Add Car");

    JLabel batchLabel = new JLabel("  Batch Count:");
    batchCountField = new JTextField(5);
    batchCountField.setText("5");
    addBatchButton = new JButton("Add Batch");

    statusLabel = new JLabel("Status: Ready");

    addCarButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String carId = carIdField.getText().trim();
        if (!carId.isEmpty()) {
          carArrives(carId);
          carIdField.setText("");
          logArea.append("Car " + carId + " arriving at the service station.\n");
          logArea.setCaretPosition(logArea.getDocument().getLength());
          updateArrivingCars();
        }
      }
    });

    addBatchButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          int count = Integer.parseInt(batchCountField.getText().trim());
          if (count > 0 && count <= 50) {
            for (int i = 0; i < count; i++) {
              carCounter++;
              String carId = "Car-" + carCounter;
              carArrives(carId);
              logArea.append("Car " + carId + " arriving at the service station.\n");
            }
            logArea.append("Added batch of " + count + " cars.\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
            updateArrivingCars();
          } else {
            JOptionPane.showMessageDialog(null,
                "Please enter a number between 1 and 50",
                "Invalid Batch Count", JOptionPane.WARNING_MESSAGE);
          }
        } catch (NumberFormatException ex) {
          JOptionPane.showMessageDialog(null,
              "Please enter a valid number",
              "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
      }
    });

    controlPanel.add(carLabel);
    controlPanel.add(carIdField);
    controlPanel.add(addCarButton);
    controlPanel.add(batchLabel);
    controlPanel.add(batchCountField);
    controlPanel.add(addBatchButton);
    controlPanel.add(Box.createHorizontalStrut(20));
    controlPanel.add(statusLabel);

    frame.add(controlPanel, BorderLayout.NORTH);

    // Center Panel - All Areas
    JPanel centerPanel = new JPanel(new GridLayout(1, 4, 10, 10));

    // Arriving Cars Panel
    arrivingPanel = new JPanel(new BorderLayout());
    arrivingPanel.setBorder(BorderFactory.createTitledBorder("Arriving Cars"));
    arrivingCarsArea = new JTextArea();
    arrivingCarsArea.setEditable(false);
    arrivingCarsArea.setFont(new Font("Arial", Font.PLAIN, 12));
    JScrollPane arrivingScrollPane = new JScrollPane(arrivingCarsArea);
    arrivingPanel.add(arrivingScrollPane, BorderLayout.CENTER);

    // Waiting Area Panel
    waitingPanel = new JPanel(new GridLayout(waitingAreaCount, 1, 5, 5));
    waitingPanel.setBorder(BorderFactory.createTitledBorder("Waiting Area"));
    for (int i = 1; i <= waitingAreaCount; i++) {
      JLabel waitingLabel = new JLabel("Slot " + i + ": EMPTY", SwingConstants.CENTER);
      waitingLabel.setOpaque(true);
      waitingLabel.setBackground(Color.LIGHT_GRAY);
      waitingLabel.setForeground(Color.BLACK);
      waitingLabel.setFont(new Font("Arial", Font.PLAIN, 12));
      waitingLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
      waitingLabel.setPreferredSize(new Dimension(200, 50));
      waitingSlots.put(String.valueOf(i), waitingLabel);
      waitingPanel.add(waitingLabel);
    }

    // Pumps Panel
    pumpPanel = new JPanel(new GridLayout(pumpCount, 1, 5, 5));
    pumpPanel.setBorder(BorderFactory.createTitledBorder("Service Bays (Pumps)"));
    for (int i = 1; i <= pumpCount; i++) {
      JLabel pumpLabel = new JLabel("Pump " + i + ": FREE", SwingConstants.CENTER);
      pumpLabel.setOpaque(true);
      pumpLabel.setBackground(Color.GREEN);
      pumpLabel.setForeground(Color.BLACK);
      pumpLabel.setFont(new Font("Arial", Font.BOLD, 14));
      pumpLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
      pumpLabel.setPreferredSize(new Dimension(200, 50));
      pumpLabels.put(String.valueOf(i), pumpLabel);
      pumpPanel.add(pumpLabel);
    }

    // Finished Cars Panel
    finishedPanel = new JPanel(new BorderLayout());
    finishedPanel.setBorder(BorderFactory.createTitledBorder("Finished Cars"));
    finishedCarsArea = new JTextArea();
    finishedCarsArea.setEditable(false);
    finishedCarsArea.setFont(new Font("Arial", Font.PLAIN, 12));
    JScrollPane finishedScrollPane = new JScrollPane(finishedCarsArea);
    finishedPanel.add(finishedScrollPane, BorderLayout.CENTER);

    centerPanel.add(arrivingPanel);
    centerPanel.add(waitingPanel);
    centerPanel.add(pumpPanel);
    centerPanel.add(finishedPanel);
    frame.add(centerPanel, BorderLayout.CENTER);

    // Bottom Panel - Log
    JPanel logPanel = new JPanel(new BorderLayout());
    logPanel.setBorder(BorderFactory.createTitledBorder("Activity Log"));

    logArea = new JTextArea(10, 50);
    logArea.setEditable(false);
    logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
    JScrollPane scrollPane = new JScrollPane(logArea);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    logPanel.add(scrollPane, BorderLayout.CENTER);
    frame.add(logPanel, BorderLayout.SOUTH);

    initializeSimulation();
  }

  private void initializeSimulation() {
    empty = new Semaphore(waitingAreaCount);
    pumps = new Semaphore(pumpCount);

    // Initialize pumps and waiting area
    intializePumps(this);
    intializeWatingArea(this);

    log("Car Wash Simulation Started!");
    log("Service Bays: " + pumpCount);
    log("Waiting Area Capacity: " + waitingAreaCount);
    log("Ready to accept cars...\n");
  }

  public void log(String message) {
    SwingUtilities.invokeLater(() -> {
      logArea.append(message + "\n");
      logArea.setCaretPosition(logArea.getDocument().getLength());
    });
  }

  public void updatePumpStatus(String pumpId, String carId, boolean occupied) {
    SwingUtilities.invokeLater(() -> {
      JLabel label = pumpLabels.get(pumpId);
      if (label != null) {
        if (occupied) {
          label.setText("Pump " + pumpId + ": " + carId);
          label.setBackground(Color.RED);
        } else {
          label.setText("Pump " + pumpId + ": FREE");
          label.setBackground(Color.GREEN);
        }
      }
    });
  }

  public void updateWaitingArea() {
    SwingUtilities.invokeLater(() -> {
      int slotNum = 1;
      for (String carId : waitingCars) {
        if (slotNum <= waitingAreaCount) {
          JLabel label = waitingSlots.get(String.valueOf(slotNum));
          if (label != null) {
            label.setText("Slot " + slotNum + ": Car " + carId);
            label.setBackground(Color.YELLOW);
          }
          slotNum++;
        }
      }
      // Clear remaining slots
      while (slotNum <= waitingAreaCount) {
        JLabel label = waitingSlots.get(String.valueOf(slotNum));
        if (label != null) {
          label.setText("Slot " + slotNum + ": EMPTY");
          label.setBackground(Color.LIGHT_GRAY);
        }
        slotNum++;
      }
    });
  }

  public void updateArrivingCars() {
    SwingUtilities.invokeLater(() -> {
      StringBuilder sb = new StringBuilder();
      for (String carId : arrivingCars) {
        sb.append(carId).append("\n");
      }
      arrivingCarsArea.setText(sb.toString());
    });
  }

  public void updateFinishedCars() {
    SwingUtilities.invokeLater(() -> {
      StringBuilder sb = new StringBuilder();
      for (String carId : finishedCars) {
        sb.append(carId).append("\n");
      }
      finishedCarsArea.setText(sb.toString());
    });
  }

  public void addFinishedCar(String carId) {
    finishedCars.add(carId);
    updateFinishedCars();
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      // Get initial configuration
      String pumpsStr = JOptionPane.showInputDialog(null,
          "Enter number of Service Bays (Pumps):", "3");
      String waitingStr = JOptionPane.showInputDialog(null,
          "Enter Waiting Area Capacity:", "5");

      int pumps = Integer.parseInt(pumpsStr != null ? pumpsStr : "3");
      int waiting = Integer.parseInt(waitingStr != null ? waitingStr : "5");

      CarWashGUI gui = new CarWashGUI(pumps, waiting);
      gui.frame.setVisible(true);
    });
  }
}
