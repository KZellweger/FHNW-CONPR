package lecture;


public class Test1 {

    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(new R(1));
        Thread t2 = new Thread(new R(2));
        
        t2.setDaemon(true);        

        t1.start();
        t2.start();
        
       

        System.out.println("done");


        // t1.setDaemon(true);
        // 

        // System.out.println(Thread.currentThread().getName());
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

/*
 * Try
 *          Thread.currentThread().yield();
 * or
 *          try { Thread.sleep(0,1); } catch (InterruptedException e) {}
 * inside the loop
 */
