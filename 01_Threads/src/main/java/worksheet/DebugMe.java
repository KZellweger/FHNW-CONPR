package worksheet;

public class DebugMe {
    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(new R(1));
        Thread t2 = new Thread(new R(2));
        t1.start();
        t2.start();
        System.out.println("#CPUs: " + Runtime.getRuntime().availableProcessors());
        System.out.println("main: done");
    }
}

class R implements Runnable {
    private int nr;

    public R(int nr) { this.nr = nr; }

    @Override
    public void run() {
       for(int i = 0; i < 100; i++) {
          System.out.println(
              "Hello" + nr + " " + i);
       }
    }
}
