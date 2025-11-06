import java.util.Queue;
import java.util.Random;

class Pump extends Thread {
  private Queue<String> queue;
  private Semaphore mutex;
  private Semaphore empty;
  private Semaphore full;
  private Semaphore pumps;
  private String id;
  private Random random;

  public Pump(String id, Queue<String> queue,
      Semaphore mutex, Semaphore empty,
      Semaphore full, Semaphore pumps) {
    this.id = id;
    this.queue = queue;
    this.mutex = mutex;
    this.empty = empty;
    this.full = full;
    this.pumps = pumps;
    this.random = new Random();
  }

  @Override
  public void run() {
    while (true) {
      try {
        pumps.P();
        full.P();
        mutex.P();

        String car = queue.poll();
        System.out.println("Pump " + id + "Occupied by: " + car);
        System.out.println("Pump " + id + " begins service: " + car);

        mutex.V();
        empty.V();
        Thread.sleep(15000 + random.nextInt(10000));

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
