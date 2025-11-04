import java.util.LinkedList;
public class Car extends Thread{
    private String  id;
    private Semaphore empty;
    private Semaphore full;
    private Semaphore mutex;
    private Semaphore pumps;
    private LinkedList<String> queue;

    public Car(String id, Semaphore empty, Semaphore full, Semaphore pumps, Semaphore mutex, LinkedList<String> queue) {
        this.id = id;
        this.empty = empty;
        this.full = full;
        this.pumps = pumps;
        this.mutex = mutex;
        this.queue = queue;
    }

    @Override
    public void run(){
        System.out.println("Car" + id + " arrived");
        empty.P();
        mutex.P();
        queue.add(id);
        if (pumps.getValue() == 0){
            System.out.println("Car" + id + " arrived and waiting");
        }
        mutex.V();
        full.V();
    }
}
