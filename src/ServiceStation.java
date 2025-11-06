import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Scanner;

class ServiceStation {
  protected static Queue<String> arrivingCars = new LinkedList<>();
  protected static Queue<String> waitingCars = new LinkedList<>();
  protected static Semaphore empty;
  protected static Semaphore full = new Semaphore();
  protected static Semaphore pumps;
  protected static Semaphore newCars = new Semaphore();
  protected static Semaphore arrivingCarsMutex = new Semaphore(1);
  protected static Semaphore waitingCarsMutex = new Semaphore(1);
  protected static int pumpCount;
  protected static int waitingAreaCount;

  protected void intializePumps() {
    intializePumps(null);
  }

  protected void intializePumps(CarWashGUI gui) {
    for (int i = 1; i <= pumpCount; i++) {
      Pump pump = new Pump(Integer.toString(i), waitingCars,
          waitingCarsMutex, empty,
          full, pumps, gui);
      pump.start();
    }
  }

  protected void intializeWatingArea() {
    intializeWatingArea(null);
  }

  protected void intializeWatingArea(CarWashGUI gui) {
    for (int i = 1; i <= waitingAreaCount; i++) {
      Car car = new Car(arrivingCars, empty, full, newCars,
          arrivingCarsMutex, waitingCarsMutex, pumps, waitingCars, gui);
      car.start();
    }
  }

  protected static String[] initializeStation() {
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
    ServiceStation station = new ServiceStation();
    station.intializePumps();
    station.intializeWatingArea();

    return carsInput.split(",");
  }

  protected static void carArrives(String carId) {
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
      // Thread.sleep(100 + random.nextInt(400));
    }

    // Keep main thread alive to allow processing
    Thread.sleep(Long.MAX_VALUE);
  }
}
