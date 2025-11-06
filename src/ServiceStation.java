import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class ServiceStation {
  static Queue<String> cars;
  static Queue<Pump> pumps;
  static Semaphore spacesAvilableWaitingArea;
  static Semaphore carsWatingServing;
  static Semaphore spacesAvilableServingArea;
  static Semaphore mutex;

  private static String[] intializeStation() {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Waiting Area Capacity: ");
    int areaCapacity = scanner.nextInt();
    System.out.print("Number of Service Bays (pumps): ");
    int numberOfBays = scanner.nextInt();
    System.out.print("Cars arriving (order): ");
    String carsInput = scanner.next();
    scanner.close();

    cars = new LinkedList<>();
    spacesAvilableServingArea = new Semaphore(numberOfBays);
    spacesAvilableWaitingArea = new Semaphore(areaCapacity);
    carsWatingServing = new Semaphore();
    mutex = new Semaphore(1);

    return carsInput.split(",");
  }

  public static void main(String[] args) {
    for (String carName : intializeStation()) {
      Car car = new Car(carName, spacesAvilableWaitingArea, carsWatingServing, spacesAvilableServingArea,
          mutex, cars);
      car.start();
    }

  }

}
