package worksheet;

import java.util.ArrayList;
import java.util.List;

public class MemoryOverFlow {
    public static void main(String[] args) {
        int counter = 0;
        List<byte[]> someBytes = new ArrayList<>();
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Max Memory: " + runtime.maxMemory());
        while (true){
            System.out.println("Max Memory: " + runtime.maxMemory());
            System.out.println("Memory Free Memory: " + runtime.freeMemory());
            Thread t = new Thread(new AllocateMemory(someBytes), "T" + counter);
            t.start();
            counter++;
        }
    }
}


class AllocateMemory implements Runnable{

    byte[] someRam = new byte[250000000];
    List<byte[]> parent;

    public AllocateMemory(List<byte[]> parent) {
        this.parent = parent;
    }

    @Override
    public void run() {
        System.out.println("Current Thread: " + Thread.currentThread());
        parent.add(someRam); // prevent GC
        try {
            Thread.sleep(6000); //keep thread alive, but give a chance to recover
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}