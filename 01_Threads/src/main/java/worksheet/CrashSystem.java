package worksheet;

public class CrashSystem {

    public static void main(String[] args) {
        int countThreads = 0;

        while (true){
            System.out.println("# Threads: " + countThreads);
            Thread thread = new Thread(new WaitForHellBeingFrozen());
            thread.start();
            countThreads++;
        }

    }

}


class WaitForHellBeingFrozen implements Runnable{

    @Override
    public void run() {
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}