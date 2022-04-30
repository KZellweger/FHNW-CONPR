import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TestClass {

    public static void main(String[] args) {
        AtomicInteger ai = new AtomicInteger(1);
        BlockingQueue<Integer> bq = new LinkedBlockingQueue<>();
        CountDownLatch cdl = new CountDownLatch(1);

        CyclicBarrier cb = new CyclicBarrier(2, () -> {
            try {
                bq.put(ai.get());
                bq.put(ai.get() + 1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        new Thread() {
            public void run() {
                ai.compareAndSet(1, 3);
                try {
                    cb.await();
                    int i = bq.take();
                    cdl.countDown();
                    System.out.println("T1: " + i);
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();

        new Thread() {
            public void run() {
                ai.compareAndSet(2, 5);
                try {
                    cb.await();
                    cdl.await();
                    int i = bq.take();
                    System.out.println("T2:" + i);
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();


    }


}