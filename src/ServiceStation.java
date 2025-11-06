import java.util.LinkedList;
import java.util.Queue;

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

  protected void intializePumps(CarWashGUI gui) {
    for (int i = 1; i <= pumpCount; i++) {
      Pump pump = new Pump(Integer.toString(i), waitingCars,
          waitingCarsMutex, empty,
          full, pumps, gui);
      pump.start();
    }
  }

  protected void intializeWatingArea(CarWashGUI gui) {
    for (int i = 1; i <= waitingAreaCount; i++) {
      Car car = new Car(arrivingCars, empty, full, newCars,
          arrivingCarsMutex, waitingCarsMutex, pumps, waitingCars, gui);
      car.start();
    }
  }

  protected static void carArrives(String carId) {
    arrivingCarsMutex.P();
    arrivingCars.add(carId);
    arrivingCarsMutex.V();
    newCars.V();
  }
}
