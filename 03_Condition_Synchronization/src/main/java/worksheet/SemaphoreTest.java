package worksheet;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class SemaphoreTest {

    public static void main(String[] args) throws InterruptedException {
        onlyOneInside();
        secondComesAfterReleaseOfFirst();
        twoAtTheSameTime();
        badNotifier();
        badInterruptor();
    }

    static void onlyOneInside() throws InterruptedException {
        final Semaphore s = new Semaphore(1);
        
        final CountDownLatch l1 = new CountDownLatch(1);
        Thread t1 = run("ok", () -> {
            s.acquire();
            l1.countDown();
        });

        Thread t2 = run("blocked", () -> {
            await(l1);
            s.acquire();
            fail("should never reach here!");
        });

        t1.join();
        t2.join(100);
        success("oneInside");
    }

    static void secondComesAfterReleaseOfFirst() throws InterruptedException {
        final Semaphore s = new Semaphore(1);
        final CountDownLatch l1 = new CountDownLatch(1);
        final AtomicReference<String> owner = new AtomicReference<String>("");
        Thread t1 = run("ok1", () -> {
            s.acquire();
            l1.countDown();
            owner.set("ok1");
            sleep(100);
            owner.set("");
            s.release();
        });

        Thread t2 = run("ok2", () -> {
            await(l1);
            s.acquire();
            if ("ok1".equals(owner.get()))
                fail("two in critical region at the same time");
            owner.set("ok2");
            s.release();
        });

        t1.join();
        t2.join();
        success("secondComesAfterReleaseOfFirst");
    }

    static void twoAtTheSameTime() throws InterruptedException {
        final Semaphore s = new Semaphore(2);
        final AtomicReference<Boolean> t1Inside = new AtomicReference<Boolean>(false);
        final AtomicReference<Boolean> t2Inside = new AtomicReference<Boolean>(false);
        final AtomicReference<Boolean> t3Inside = new AtomicReference<Boolean>(false);

        Thread t1 = run("ok1", () -> {
            s.acquire();
            t1Inside.set(true);
            sleep(100);
            if (!t2Inside.get())
                fail("t2 should also be inside!");
            if (t3Inside.get())
                fail("t3 should not be inside!");
            sleep(100);
            t1Inside.set(false);
            s.release();
        });

        sleep(10);

        Thread t2 = run("ok2", () -> {
            s.acquire();
            t2Inside.set(true);
            sleep(100);
            if (!t1Inside.get())
                fail("t1 should also be inside!");
            if (t3Inside.get())
                fail("t3 should not be inside!");
            sleep(100);
            t2Inside.set(false);
            s.release();
        });

        sleep(10);

        Thread t3 = run("nok", () -> {
            s.acquire();
            t3Inside.set(true);
            sleep(100);
            t3Inside.set(false);
            s.release();
        });

        t1.join();
        t2.join();
        t3.join();
        success("twoAtTheSameTime");
    }

    static void badNotifier() throws InterruptedException {
        final Semaphore s = new Semaphore(0);
        Thread t1 = run("ok", () -> {
            s.acquire();
            fail("should never reach here! Use a private lock and loop over predicate");
        });

        Thread t2 = run("notifier", () -> {
            sleep(100);
            synchronized (s) {
                s.notifyAll();
            }
        });

        t1.join(200);
        t2.join();
        success("badNotifier");
    }

    static void badInterruptor() throws InterruptedException {
        final Semaphore s = new Semaphore(0);
        final Thread t1 = run("ok", () -> {
            s.acquire();
            fail("should never reach here!");
        });

        Thread t2 = run("notifier", () -> {
            sleep(100);
            t1.interrupt();
        });

        t1.join(200);
        t2.join();
        success("badInterruptor");
    }

    static final AtomicBoolean failed = new AtomicBoolean(false);

    static void success(String msg) {
        if (!failed.get()) {
            System.out.println("+ " + msg);
        } else {
            failed.set(false);
        }
    }

    static void fail(String msg) {
        failed.set(true);
        throw new IllegalStateException(msg);
    }

    static void await(CountDownLatch l) {
        try {
            l.await();
        } catch (InterruptedException e) {
        }
    }

    static Thread run(String name, Runnable r) {
        Thread t = new Thread(r, name);
        t.setDaemon(true);
        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.err.println(t.getName() + " failed: " + e.getMessage());
            }
        });
        t.start();
        return t;
    }

    static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }
}
