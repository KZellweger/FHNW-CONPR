package as.examples;

import as.conbench.Benchmark;
import as.conbench.Threads;

//http://ark.intel.com/products/70846
public class CacheEffects {

    static final int N_TIMES = 10000;
    static final int INCREMENTS = 1000;

    @Benchmark(N_TIMES)
    public static class FalseSharing {
        public volatile int a;
        public volatile int b;

        @Threads({1})
        public void accessA(int nTimes, int nThreads) {
            for (int i = 0; i < nTimes; i++) {
                for (int j = 0; j < INCREMENTS; j++) {
                    a = a + 1;
                }
            }
        }

        @Threads({1})
        public void accessB(int nTimes, int nThreads) {
            for (int i = 0; i < nTimes; i++) {
                for (int j = 0; j < INCREMENTS; j++) {
                    b = b + 1;
                }
            }
        }
    }

//    @Benchmark(N_TIMES)
//    public static class ContendedAnnotation {
//        @sun.misc.Contended
//        public volatile long a;
//        @sun.misc.Contended
//        public volatile long b;
//
//        @Threads({1})
//        public void accessA(int nTimes, int nThreads) {
//            for (int i = 0; i < nTimes; i++) {
//                for (int j = 0; j < INCREMENTS; j++) {
//                    a = a + 1;
//                }
//            }
//        }
//
//        @Threads({1})
//        public void accessB(int nTimes, int nThreads) {
//            for (int i = 0; i < nTimes; i++) {
//                for (int j = 0; j < INCREMENTS; j++) {
//                    b = b + 1;
//                }
//            }
//        }
//    }


    @Benchmark(N_TIMES)
    public static class PaddedVariables {
        public volatile int a;
        public volatile int p1, p2, p3, p4, p5, p6, p7 = 0;
        public volatile int b;

        @Threads({1})
        public void accessA(int nTimes, int nThreads) {
            for (int i = 0; i < nTimes; i++) {
                for (int j = 0; j < INCREMENTS; j++) {
                    a = a + 1;
                }
            }
        }

        @Threads({1})
        public void accessB(int nTimes, int nThreads) {
            for (int i = 0; i < nTimes; i++) {
                for (int j = 0; j < INCREMENTS; j++) {
                    b = b + 1;
                }
            }
        }
    }
}
