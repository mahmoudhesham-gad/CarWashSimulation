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

  public Car(Queue<String> cars, Semaphore empty, Semaphore full,
      Semaphore newCars, Semaphore arrivingCarsMutex, Semaphore waitingCarsMutex,
      Semaphore pumps, Queue<String> queue) {
    this.cars = cars;
    this.empty = empty;
    this.full = full;
    this.newCars = newCars;
    this.waitingCarsMutex = waitingCarsMutex;
    this.arrivingCarsMutex = arrivingCarsMutex;
    this.queue = queue;
    this.pumps = pumps;
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
        System.out.println("Car " + id + " enters the waiting area");
      } else {
        System.out.println("Car " + id + " enters the service area");
      }
      waitingCarsMutex.V();
      full.V();
    }

  }
}
