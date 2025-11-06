import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

class ServiceStation {
  static Queue<String> arrivingCars = new LinkedList<>();
  static Queue<String> waitingCars = new LinkedList<>();
  static Semaphore empty;
  static Semaphore full = new Semaphore();
  static Semaphore pumps;
  static Semaphore newCars = new Semaphore();
  static Semaphore arrivingCarsMutex = new Semaphore(1);
  static Semaphore pumpQueueMutex = new Semaphore(1);
  static Semaphore waitingCarsMutex = new Semaphore(1);
  static int pumpCount;
  static int waitingAreaCount;

  private static void intializePumps() {
    for (int i = 1; i <= pumpCount; i++) {
      Pump pump = new Pump(Integer.toString(i), waitingCars,
          pumpQueueMutex, empty,
          full, pumps);
      pump.start();
    }
  }

  private static void intializeWatingArea() {
    for (int i = 1; i <= waitingAreaCount; i++) {
      Car car = new Car(arrivingCars, empty, full, newCars,
          arrivingCarsMutex, waitingCarsMutex, pumps, waitingCars, pumpCount);
      car.start();
    }
  }

  private static String[] initializeStation() {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Waiting Area Capacity: ");
    waitingAreaCount = scanner.nextInt();
    System.out.print("Number of Service Bays (pumps): ");
    pumpCount = scanner.nextInt();
    System.out.print("Cars arriving (order): ");
    String carsInput = scanner.next();
    scanner.close();
    pumps = new Semaphore(pumpCount);
    empty = new Semaphore(waitingAreaCount);
    intializePumps();
    intializeWatingArea();

    return carsInput.split(",");
  }

  private static void carArrives(String carId) {
    arrivingCarsMutex.P();
    arrivingCars.add(carId);
    arrivingCarsMutex.V();
    newCars.V();
  }

  public static void main(String[] args) throws InterruptedException {
    String[] carsToArrive = initializeStation();
    Random random = new Random();
    for (String car : carsToArrive) {
      System.out.println("Car " + car + " arriving at the service station.");
      carArrives(car);
      Thread.sleep(100 + random.nextInt(400));
    }

    // Keep main thread alive to allow processing
    Thread.sleep(Long.MAX_VALUE);
  }
}
