import java.util.Queue;
import java.util.Random;

class Pump extends Thread {
  private Queue<String> queue;
  private Semaphore waitingCarsMutex;
  private Semaphore empty;
  private Semaphore full;
  private Semaphore pumps;
  private String id;
  private Random random;

  public Pump(String id, Queue<String> queue,
      Semaphore waitingCarsMutex, Semaphore empty,
      Semaphore full, Semaphore pumps) {
    this.id = id;
    this.queue = queue;
    this.waitingCarsMutex = waitingCarsMutex;
    this.empty = empty;
    this.full = full;
    this.pumps = pumps;
    this.random = new Random();
  }

  @Override
  public void run() {
    while (true) {
      try {
        full.P();
        pumps.P();
        waitingCarsMutex.P();

        String car = queue.poll();
        System.out.println("Pump " + id + " Occupied by: " + car);
        System.out.println("Pump " + id + " begins service: " + car);

        waitingCarsMutex.V();
        empty.V();
        Thread.sleep(1500 + random.nextInt(1000));

        System.out.println("Pump " + id + ": " + car + " finishes service");
        System.out.println("Pump " + id + ": is now free");

        pumps.V();

      } catch (InterruptedException e) {
        System.out.println("Pump " + id + " interrupted.");
        break;
      }
    }
  }
}
