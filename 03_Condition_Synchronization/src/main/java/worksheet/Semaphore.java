package worksheet;

public final class Semaphore {
    private int value;
    private Object lock = new Object();

    public Semaphore(int initial) {
        if (initial < 0) throw new IllegalArgumentException();
        value = initial;
    }

    public int available() {
        return value;
//        synchronized (lock) {
//        }
    }

    public void acquire() {
        synchronized (lock) {
            while (available() == 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                }
            }
            value--;
            lock.notifyAll();
        }
    }

    public void release() {
        synchronized (lock) {
            value++;
            lock.notifyAll();
        }
    }

}
