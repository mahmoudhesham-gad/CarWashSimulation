import java.util.Queue;
import java.util.Random;

class Pump extends Thread {
  private Queue<String> queue;
  private Semaphore waitingCarsMutex;
  private Semaphore empty;
  private Semaphore full;
  private Semaphore pumps;
  protected String id;
  private Random random;
  protected CarWashGUI gui;

  public Pump(String id, Queue<String> queue,
      Semaphore waitingCarsMutex, Semaphore empty,
      Semaphore full, Semaphore pumps, CarWashGUI gui) {
    this.id = id;
    this.queue = queue;
    this.waitingCarsMutex = waitingCarsMutex;
    this.empty = empty;
    this.full = full;
    this.pumps = pumps;
    this.random = new Random();
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
        this.gui.log("Pump " + id + " Occupied by: " + car);
        this.gui.log("Pump " + id + " begins service: " + car);
        gui.updatePumpStatus(id, car, true);
        gui.updateWaitingArea();
        waitingCarsMutex.V();
        empty.V();
        Thread.sleep(3000 + random.nextInt(2000));

        this.gui.log("Pump " + id + ": " + car + " finishes service");
        gui.addFinishedCar(car);
        this.gui.log("Pump " + id + ": is now free");
        gui.updatePumpStatus(id, car, false);

        pumps.V();
        gui.updateWaitingArea();

        // Delay before picking up next car from waiting area
        Thread.sleep(1000);

      } catch (InterruptedException e) {
        this.gui.log("Pump " + id + " interrupted.");
        break;
      }
    }
  }
}
