package jmm;

public class PseudoQuiz {
    int x = 0;
    final Object lock = new Object();

    private void run() throws InterruptedException {

        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                x += 4;
            }
        }, "T1");

        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                x++;
            }
        }, "T2");

        Thread t3 = new Thread(() -> System.out.println("X = " + x), "T3");

        t1.start();
        t2.start();
        t1.join();
        t3.start();
    }

    public static void main(String[] args) throws InterruptedException {
        new PseudoQuiz().run();
    }
}
