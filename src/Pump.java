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
      Semaphore full, Semaphore pumps) {
    this(id, queue, waitingCarsMutex, empty, full, pumps, null);
  }

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

  protected void log(String message) {
    if (gui != null) {
      gui.log(message);
    } else {
      System.out.println(message);
    }
  }

  @Override
  public void run() {
    while (true) {
      try {
        full.P();
        pumps.P();
        waitingCarsMutex.P();

        String car = queue.poll();
        log("Pump " + id + " Occupied by: " + car);
        log("Pump " + id + " begins service: " + car);
        if (gui != null) {
          gui.updatePumpStatus(id, car, true);
          gui.updateWaitingArea();
        }

        waitingCarsMutex.V();
        empty.V();
        Thread.sleep(3000 + random.nextInt(2000));

        log("Pump " + id + ": " + car + " finishes service");
        if (gui != null) {
          gui.addFinishedCar(car);
        }
        log("Pump " + id + ": is now free");
        if (gui != null) {
          gui.updatePumpStatus(id, car, false);
        }

        pumps.V();
        
        // Delay before picking up next car from waiting area
        if (gui != null) {
          Thread.sleep(1000);
        }

      } catch (InterruptedException e) {
        log("Pump " + id + " interrupted.");
        break;
      }
    }
  }
}
