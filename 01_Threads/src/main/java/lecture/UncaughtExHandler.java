package lecture;

public class UncaughtExHandler {
    public static void main(String[] args) {
        Thread t = new Thread(() -> { throw new IllegalStateException("Test"); }, "Other");

        t.setUncaughtExceptionHandler((Thread th, Throwable e) -> {
                System.out.println("Thread " + th + " died because of " + e);
                System.out.println(th == Thread.currentThread());
                
            }
        );

        t.start();
    }
}
