import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.Map;

public class CarWashGUI extends JFrame {
    private static Queue<String> arrivingCars = new LinkedList<>();
    private static Queue<String> waitingCars = new LinkedList<>();
    private static Semaphore empty;
    private static Semaphore full = new Semaphore();
    private static Semaphore pumps;
    private static Semaphore newCars = new Semaphore();
    private static Semaphore arrivingCarsMutex = new Semaphore(1);
    private static Semaphore waitingCarsMutex = new Semaphore(1);
    private static int pumpCount;
    private static int waitingAreaCount;
    
    // GUI Components
    private JPanel pumpPanel;
    private JPanel waitingPanel;
    private JTextArea logArea;
    private JTextField carIdField;
    private JButton addCarButton;
    private JTextField batchCountField;
    private JButton addBatchButton;
    private JLabel statusLabel;
    private Map<String, JLabel> pumpLabels;
    private Map<String, JLabel> waitingSlots;
    private int carCounter = 0;
    
    public CarWashGUI(int pumps, int waitingArea) {
        pumpCount = pumps;
        waitingAreaCount = waitingArea;
        pumpLabels = new HashMap<>();
        waitingSlots = new HashMap<>();
        
        setTitle("Car Wash Simulation");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
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
        
        add(controlPanel, BorderLayout.NORTH);
        
        // Center Panel - Pumps and Waiting Area
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        
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
        
        centerPanel.add(pumpPanel);
        centerPanel.add(waitingPanel);
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom Panel - Log
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Activity Log"));
        
        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        logPanel.add(scrollPane, BorderLayout.CENTER);
        add(logPanel, BorderLayout.SOUTH);
        
        initializeSimulation();
    }
    
    private void initializeSimulation() {
        CarWashSimulation.empty = new Semaphore(waitingAreaCount);
        CarWashSimulation.pumps = new Semaphore(pumpCount);
        empty = CarWashSimulation.empty;
        pumps = CarWashSimulation.pumps;
        
        // Start pump threads
        for (int i = 1; i <= pumpCount; i++) {
            PumpGUI pump = new PumpGUI(Integer.toString(i), waitingCars,
                    waitingCarsMutex, empty, full, pumps, this);
            pump.start();
        }
        
        // Start car threads
        for (int i = 1; i <= waitingAreaCount; i++) {
            CarGUI car = new CarGUI(arrivingCars, empty, full, newCars,
                    arrivingCarsMutex, waitingCarsMutex, pumps, waitingCars, this);
            car.start();
        }
        
        log("Car Wash Simulation Started!");
        log("Service Bays: " + pumpCount);
        log("Waiting Area Capacity: " + waitingAreaCount);
        log("Ready to accept cars...\n");
    }
    
    private static void carArrives(String carId) {
        arrivingCarsMutex.P();
        arrivingCars.add(carId);
        arrivingCarsMutex.V();
        newCars.V();
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
            gui.setVisible(true);
        });
    }
}

class CarWashSimulation {
    static Semaphore empty;
    static Semaphore pumps;
}

class CarGUI extends Thread {
    private Queue<String> cars;
    private Semaphore empty;
    private Semaphore full;
    private Semaphore arrivingCarsMutex;
    private Semaphore waitingCarsMutex;
    private Semaphore pumps;
    private Semaphore newCars;
    private Queue<String> queue;
    private CarWashGUI gui;

    public CarGUI(Queue<String> cars, Semaphore empty, Semaphore full,
            Semaphore newCars, Semaphore arrivingCarsMutex, Semaphore waitingCarsMutex,
            Semaphore pumps, Queue<String> queue, CarWashGUI gui) {
        this.cars = cars;
        this.empty = empty;
        this.full = full;
        this.newCars = newCars;
        this.waitingCarsMutex = waitingCarsMutex;
        this.arrivingCarsMutex = arrivingCarsMutex;
        this.queue = queue;
        this.pumps = pumps;
        this.gui = gui;
    }

    @Override
    public void run() {
        while (true) {
            empty.P();
            newCars.P();
            arrivingCarsMutex.P();
            String id = cars.poll();
            arrivingCarsMutex.V();
            waitingCarsMutex.P();
            queue.add(id);
            if (this.pumps.getValue() == 0) {
                gui.log("Car " + id + " enters the waiting area");
            } else {
                gui.log("Car " + id + " enters the service area");
            }
            gui.updateWaitingArea();
            waitingCarsMutex.V();
            full.V();
        }
    }
}

class PumpGUI extends Thread {
    private Queue<String> queue;
    private Semaphore waitingCarsMutex;
    private Semaphore empty;
    private Semaphore full;
    private Semaphore pumps;
    private String id;
    private CarWashGUI gui;

    public PumpGUI(String id, Queue<String> queue,
            Semaphore waitingCarsMutex, Semaphore empty,
            Semaphore full, Semaphore pumps, CarWashGUI gui) {
        this.id = id;
        this.queue = queue;
        this.waitingCarsMutex = waitingCarsMutex;
        this.empty = empty;
        this.full = full;
        this.pumps = pumps;
        this.gui = gui;
    }

    @Override
    public void run() {
        while (true) {
            try {
                full.P();
                pumps.P();
                waitingCarsMutex.P();

                String car = queue.poll();
                gui.log("Pump " + id + " Occupied by: " + car);
                gui.log("Pump " + id + " begins service: " + car);
                gui.updatePumpStatus(id, car, true);
                gui.updateWaitingArea();

                waitingCarsMutex.V();
                empty.V();
                Thread.sleep(3000 + (int)(Math.random() * 2000));

                gui.log("Pump " + id + ": " + car + " finishes service");
                gui.log("Pump " + id + ": is now free");
                gui.updatePumpStatus(id, car, false);

                pumps.V();

            } catch (InterruptedException e) {
                gui.log("Pump " + id + " interrupted.");
                break;
            }
        }
    }
}
