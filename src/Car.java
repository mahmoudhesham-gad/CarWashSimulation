import java.util.Queue;

public class Car extends Thread {
  private Queue<String> cars;
  private Semaphore empty;
  private Semaphore full;
  private Semaphore arrivingCarsMutex;
  private Semaphore waitingCarsMutex;
  private Semaphore pumps;
  private Semaphore newCars;
  private Queue<String> queue;
  protected CarWashGUI gui;

  public Car(Queue<String> cars, Semaphore empty, Semaphore full,
      Semaphore newCars, Semaphore arrivingCarsMutex, Semaphore waitingCarsMutex,
      Semaphore pumps, Queue<String> queue) {
    this(cars, empty, full, newCars, arrivingCarsMutex, waitingCarsMutex, pumps, queue, null);
  }

  public Car(Queue<String> cars, Semaphore empty, Semaphore full,
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
      empty.P();
      newCars.P();
      arrivingCarsMutex.P();
      String id = cars.poll();
      arrivingCarsMutex.V();
      if (gui != null) {
        gui.updateArrivingCars();
      }
      waitingCarsMutex.P();
      queue.add(id);
      if (this.pumps.getValue() == 0) {
        log("Car " + id + " enters the waiting area");
      } else {
        log("Car " + id + " enters the service area");
      }
      if (gui != null) {
        gui.updateWaitingArea();
      }
      waitingCarsMutex.V();
      full.V();
    }

  }
}
