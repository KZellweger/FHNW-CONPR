package jmm;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JMMTest {
    int x = 0, y = 0;
    int a = 0, b = 0;

    static void burnCycles() {
        int n = 1000000000;
        while (n > 0) n--;
    }

    class T1 extends Thread {
        @Override
        public void run() {
            burnCycles();
            x = 1;
            burnCycles();
            b = y;
        }
    }

    class T2 extends Thread {
        @Override
        public void run() {
            burnCycles();
            y = 1;
            burnCycles();
            a = x;
        }
    }

    public static void main(String[] args) throws Exception {
        Set<String> set = new HashSet<String>();
        final JMMTest t = new JMMTest();

        while (set.size() < 4) {
            t.a = 0; t.b = 0;
            t.x = 0; t.y = 0;
            Thread t1 = t.new T1();
            Thread t2 = t.new T2();
            t1.start();
            t2.start();
            t1.join();
            t2.join();
            String s = "(" + t.a + "," + t.b + ")";
            if (!set.contains(s)) {
                set.add(s);
                System.out.println(new Date() + ":  " + s);
            }
        }
    }
}

/*
 * T1 vor T2: (1,0)
 * T2 vor T1: (0,1)
 * T1 und T2 interleaved: (1,1)
 * Visibilityproblem: (0,0)
 *
 */

/*
➜  04_JMM git:(master) ✗  /usr/bin/env /Library/Java/JavaVirtualMachines/openjdk-13.0.1.jdk/Contents/Home/bin/java -Dfile.encoding=UTF-8 -cp /Users/dk/Documents/fhnw/Teaching/conpr/teaching/04_JMM/bin/main jmm.JMMTest 
Sun Feb 28 20:22:29 CET 2021:  (1,1)
Sun Feb 28 20:22:29 CET 2021:  (1,0)
Sun Feb 28 20:22:29 CET 2021:  (0,1)
Sun Feb 28 20:26:21 CET 2021:  (0,0)

M1 ARM
➜  04_JMM git:(master) ✗  /usr/bin/env /Library/Java/JavaVirtualMachines/jdk-17.0.2.jdk/Contents/Home/bin/java -XX:+Sh
owCodeDetailsInExceptionMessages -cp /Users/dk/Documents/fhnw/Teaching/conpr/teaching/04_JMM/bin/main jmm.JMMTest 
Wed Mar 02 14:36:19 CET 2022:  (1,1)
Wed Mar 02 14:36:19 CET 2022:  (1,0)
Wed Mar 02 14:36:19 CET 2022:  (0,1)
Wed Mar 02 14:36:52 CET 2022:  (0,0)
*/
