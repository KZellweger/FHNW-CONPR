package lecture;

public class ThreadAPI {
    public static void main(String[] args) throws InterruptedException {
    	
    	Thread current = Thread.currentThread();

        System.out.println(current.getName());
        System.out.println(current.isDaemon());

        Thread other = new Thread(() -> {
            System.out.println(Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }, "Other");
        other.start();

        Thread.sleep(500);
        System.out.println(other.getState());

        System.out.println(Thread.currentThread().getState());
    }
}
