import java.util.LinkedList;

class Pump extends Thread {
    private LinkedList<String> queue;
    private Semaphore mutex;
    private Semaphore empty;
    private Semaphore full;
    private Semaphore pumps;
    private String id;

    public Pump(String id, LinkedList<String> queue, Semaphore mutex,
                Semaphore empty, Semaphore full, Semaphore pumps) {
        this.id = id;
        this.queue = queue;
        this.mutex = mutex;
        this.empty = empty;
        this.full = full;
        this.pumps = pumps;
    }

    @Override
    public void run() {
        while (true) {
            try {
                pumps.P();
                full.P();
                mutex.P();

                String car = queue.removeFirst();
                System.out.println("Pump " + id + ": " + car + " Occupied");
                System.out.println("Pump " + id + ": " + car + " login");
                System.out.println("Pump " + id + ": " + car + " begins service at Bay " + id);

                mutex.V();
                Thread.sleep(2000);

                System.out.println("Pump " + id + ": " + car + " finishes service");
                System.out.println("Pump " + id + ": Bay " + id + " is now free");

                empty.V();
                pumps.V();

            } catch (InterruptedException e) {
                System.out.println("Pump " + id + " interrupted.");
                break;
            }
        }
    }
}
